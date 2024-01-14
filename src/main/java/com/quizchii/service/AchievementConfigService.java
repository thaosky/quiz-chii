package com.quizchii.service;

import com.quizchii.Enum.SortDir;
import com.quizchii.entity.QuestionTagEntity;
import com.quizchii.entity.TagEntity;
import com.quizchii.entity.TestTagEntity;
import com.quizchii.model.ListResponse;
import com.quizchii.repository.QuestionTagRepository;
import com.quizchii.repository.TagRepository;
import com.quizchii.repository.TestTagRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AchievementConfigService {

}
