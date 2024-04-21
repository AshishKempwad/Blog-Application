package com.springboot.blog.service.impl;

import com.springboot.blog.entity.Comment;
import com.springboot.blog.entity.Post;
import com.springboot.blog.exception.BlogAPIException;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.payload.CommentDto;
import com.springboot.blog.repository.CommentRepository;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private CommentRepository commentRepository;
    private PostRepository postRepository;

    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    @Override
    public CommentDto createComment(long postId, CommentDto commentsDto) {

        Comment comment = mapToEntity(commentsDto);

        //Retrieve post entity by id
        Post post = retrievePost(postId);

        //set post to comment entity
        comment.setPost(post);

        //Save comment in DB
        Comment newComment = commentRepository.save(comment);

        //We need to convert entity back into DTO and return
        return mapToDto(newComment);

    }

    @Override
    public List<CommentDto> getCommentsByPostId(long postId) {

        //Retrieve comments based on postId from DB
        List<Comment> comments = commentRepository.findByPostId(postId);

        return comments.stream().map(comment -> mapToDto(comment)).collect(Collectors.toList());

    }

    @Override
    public CommentDto getCommentById(Long postId, Long commentId) {

        //Retrieve post based on the postId
        Post post = retrievePost(postId);


        //Retrieve comment based on commentId
        Comment comment = retrieveComment(commentId);


        //We need to check if the fetched comment based on commentId belongs to the fetched post based on postId
        if(comment.getPost().getId() != post.getId()){
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Comment does not belong to the post");
        }

        return mapToDto(comment);
    }

    @Override
    public CommentDto updateComment(Long postId, Long commentId, CommentDto commentRequest) {

        //Retrieve post based on postId
        Post post = retrievePost(postId);

        //Retrieve comment based on commentId
        Comment comment = retrieveComment(commentId);

        if(comment.getPost().getId() != post.getId()){
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Comment does not belong to the post");
        }

        //Update the comment based on new comment request

        comment.setName(commentRequest.getName());
        comment.setEmail(commentRequest.getEmail());
        comment.setBody(commentRequest.getBody());

        commentRepository.save(comment);

        return mapToDto(comment);

    }

    @Override
    public void deleteComment(Long postId, Long commentId) {
        //Retrieve post based on postId
        Post post = retrievePost(postId);

        //Retrieve comment based on commentId
        Comment comment = retrieveComment(commentId);

        if(comment.getPost().getId() != post.getId()){
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Comment does not belong to the post");
        }

        commentRepository.deleteById(commentId);
    }

    private Comment retrieveComment(Long commentId){
        return commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment","id", commentId));
    }

    private Post retrievePost(Long postId){
        return postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
    }

    private CommentDto mapToDto(Comment comment){
        CommentDto commentDto = new CommentDto();

        commentDto.setId(comment.getId());
        commentDto.setName(comment.getName());
        commentDto.setEmail(comment.getEmail());
        commentDto.setBody(comment.getBody());

        return commentDto;
    }

    private Comment mapToEntity(CommentDto commentDto){
        Comment comment = new Comment();

        comment.setId(commentDto.getId());
        comment.setName(commentDto.getName());
        comment.setEmail(commentDto.getEmail());
        comment.setBody(commentDto.getBody());

        return comment;
    }
}
