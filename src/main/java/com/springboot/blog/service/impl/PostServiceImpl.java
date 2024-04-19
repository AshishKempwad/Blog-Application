package com.springboot.blog.service.impl;

import com.springboot.blog.entity.Post;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.service.PostService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private PostRepository postRepository;

    //Injecting the postRepository using constructor based dependency injection
    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    @Transactional
    public PostDto createPost(PostDto postDto) {

        //TODO: Add exception handling. When query fails it should rollback and ID should not be incremented
        try {
            //convert dto to entity
            Post post = mapToEntity(postDto);

            //Getting response from the repository
            Post newPost = postRepository.save(post);

            //convert entity to dto
            return  mapToDto(newPost);

        }catch(DataIntegrityViolationException e){
            throw new IllegalArgumentException("Title must be unique",e);

        }catch(Exception ex){
            throw new RuntimeException("Failed to create the post", ex);
        }
    }

    @Override
    public List<PostDto> getAllPosts() {

        //Get all the posts from the database
        List<Post> posts = postRepository.findAll();

        //Now we need to map all the posts to dto and return
        return posts.stream().map(post -> mapToDto(post)).collect(Collectors.toList());

    }

    @Override
    public PostDto getPostById(long id) {
        Post post= postRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Post","id",id));
        return mapToDto(post);
    }

    @Override
    public PostDto updatePost(PostDto postDto, long id) {

        //get post by id from database. In case post is not present for a id then throw the exception
        Post post = postRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Post","id",id));

        post.setTitle(postDto.getTitle());
        post.setDescription(postDto.getDescription());
        post.setContent(postDto.getContent());

        Post updatedPost = postRepository.save(post);
        return mapToDto(updatedPost);
    }

    @Override
    public void deleteById(long id) {

        postRepository.deleteById(id);
    }

    //convert dto to entity
    private Post mapToEntity(PostDto postDto){

        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setDescription(postDto.getDescription());
        post.setContent(postDto.getContent());

        return post;
    }

    //convert entity to dto
    private PostDto mapToDto(Post post){

        PostDto postDto = new PostDto();
        postDto.setId(post.getId());
        postDto.setTitle(post.getTitle());
        postDto.setDescription(post.getDescription());
        postDto.setContent(post.getContent());

        return postDto;
    }
}
