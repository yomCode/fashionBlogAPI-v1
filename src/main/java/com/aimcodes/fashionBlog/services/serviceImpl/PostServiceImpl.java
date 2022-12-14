package com.aimcodes.fashionBlog.services.serviceImpl;

import com.aimcodes.fashionBlog.entities.Category;
import com.aimcodes.fashionBlog.entities.Post;
import com.aimcodes.fashionBlog.entities.User;
import com.aimcodes.fashionBlog.enums.Role;
import com.aimcodes.fashionBlog.exceptions.HandleNullException;
import com.aimcodes.fashionBlog.exceptions.NoAccessException;
import com.aimcodes.fashionBlog.exceptions.NoDataFoundException;
import com.aimcodes.fashionBlog.pojos.ApiResponse;
import com.aimcodes.fashionBlog.pojos.PostRequestDto;
import com.aimcodes.fashionBlog.pojos.PostResponseDto;
import com.aimcodes.fashionBlog.repositories.CategoryRepository;
import com.aimcodes.fashionBlog.repositories.PostRepository;
import com.aimcodes.fashionBlog.services.PostService;
import com.aimcodes.fashionBlog.utils.ResponseManager;
import com.aimcodes.fashionBlog.utils.UuidGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    @Autowired
    private HttpSession session;
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final ResponseManager responseManager;
    private final UuidGenerator uuidGenerator;


    @Override
    public ResponseEntity<ApiResponse> createPost(PostRequestDto request) {
        User user = (User) session.getAttribute("currUser");
        if (user != null && user.getRole().equals(Role.valueOf("ADMIN"))) {
            String uuid = uuidGenerator.generateUuid();
            Category category = categoryRepository.findByName(request.getCategory().toLowerCase()).orElseThrow(() ->
                   new NoDataFoundException("Category does not exist", "Enter a valid category name"));

            Post post = Post.builder().title(request.getTitle())
                    .content(request.getContent())
                    .category(category)
                    .user(user)
                    .uuid(uuid)
                    .build();
            postRepository.save(post);

            PostResponseDto response = PostResponseDto.builder()
                    .title(post.getTitle())
                    .content(post.getContent())
                    .created_date(post.getCreatedAt())
                    .category(post.getCategory().getName()).build();
            return new ResponseEntity<>(responseManager.successfulRequest(response), HttpStatus.CREATED);

        }else if (user == null)
            throw new HandleNullException("Invalid user request", "No user in session");
        throw new NoAccessException("Unauthorized user", "User doesnt have the right to create a post!");
    }


    @Override
    public ResponseEntity<ApiResponse> edit_post(PostRequestDto request, String uuid) {

        User user = (User) session.getAttribute("currUser");
        if (user != null && user.getRole().equals(Role.valueOf("ADMIN"))) {
            Post post = postRepository.findByUuid(uuid).orElseThrow(()->
                    new NoDataFoundException("No such post", "Post with uuid " + uuid + " does not exist"));

            post.setTitle(request.getTitle());
            post.setContent(request.getContent());
            postRepository.save(post);

            PostResponseDto response = PostResponseDto.builder()
                    .title(post.getTitle())
                    .content(post.getContent())
                    .created_date(post.getCreatedAt())
                    .category(post.getCategory().getName()).build();

            return new ResponseEntity<>(responseManager.successfulRequest(response), HttpStatus.ACCEPTED);
        }else if(user == null)
            throw new HandleNullException("Login to edit post", "No user is in session");
        throw new NoAccessException("Unauthorized user", "User doesnt have the right to edit this post!");
    }

    @Override
    public ResponseEntity<ApiResponse> delete_Post(String uuid){
        User user = (User) session.getAttribute("currUser");
        if(user != null && user.getRole().equals(Role.valueOf("ADMIN"))){
            Post post = postRepository.findByUuid(uuid).orElseThrow(()->
                    new NoDataFoundException("No such post", "Post with uuid " + uuid + " does not exist"));
            postRepository.delete(post);

            return new ResponseEntity<>(
                    responseManager.successfulRequest("Post deleted successfully!"),
                    HttpStatus.ACCEPTED
            );
        }else if(user == null)
            throw new HandleNullException("Invalid user request", "No user in session");
        throw new NoAccessException("Unauthorized user", "User doesnt have the right to delete this post!");
    }

    @Override
    public ResponseEntity<ApiResponse> view_all_post(){
        List<Post> posts = postRepository.findAll();
        List<PostResponseDto> responses = new ArrayList<>();

        posts.forEach(post -> {
            PostResponseDto response = PostResponseDto.builder()
                    .title(post.getTitle())
                    .content(post.getContent())
                    .created_date(post.getCreatedAt())
                    .category(post.getCategory().getName()).build();
            responses.add(response);
        });
        return new ResponseEntity<>(responseManager.successfulRequest(responses), HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<ApiResponse> view_post_by_category(String uuid){
        Category category = categoryRepository.findByUuid(uuid).orElseThrow(() ->
                new NoDataFoundException("No such category", uuid + " does not exist in the database"));

            List<Post> posts = category.getPosts();
            List<PostResponseDto> responses = new ArrayList<>();
            posts.forEach(post -> {
                PostResponseDto response = PostResponseDto.builder()
                        .title(post.getTitle())
                        .content(post.getContent())
                        .created_date(post.getCreatedAt())
                        .category(post.getCategory().getName()).build();
                responses.add(response);
            });
            return new ResponseEntity<>(responseManager.successfulRequest(responses), HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<ApiResponse> getPostByUuid(String uuid){
        Post post = postRepository.findByUuid(uuid).orElseThrow(()->
                new NoDataFoundException("No such post", "Post with uuid " + uuid + " does not exist"));

        PostResponseDto response = PostResponseDto.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .category(String.valueOf(post.getCategory()))
                .created_date(post.getCreatedAt())
                .build();
        return new ResponseEntity<>(responseManager.successfulRequest(response), HttpStatus.FOUND);
    }

    @Override
    public ResponseEntity<ApiResponse> searchPost(String question){

        List<Post> posts = postRepository.findBySearch(question).orElseThrow(()-> new NoDataFoundException("No Post found", question + " is not found in any post"));

        return new ResponseEntity<>(responseManager.successfulRequest(posts), HttpStatus.FOUND);
    }

}
