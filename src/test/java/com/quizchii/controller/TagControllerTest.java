//package com.quizchii.controller;
//
//import com.quizchii.entity.TagEntity;
//import com.quizchii.service.impl.TagService;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//
//@WebMvcTest(TagController.class)
////@ExtendWith(MockitoExtension.class)
////@AutoConfigureMockMvc
//class TagControllerTest {
//    @MockBean
//    TagService tagService;
//
//    @Autowired
//    MockMvc mockMvc;
//
//    @Test
//    void getTagById() {
//        // Given
//        Long tagId = 1L;
//        TagEntity tag = new TagEntity(1L, "Toeic", "This is toeic tag");
//        when(tagService.getTagById(tagId)).thenReturn(tag);
//
//        mockMvc.perform(get(""))
//    }
//}