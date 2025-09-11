package com.sprint.project.hrbank.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.project.hrbank.entity.QChangeLog;
import com.sprint.project.hrbank.repository.ChangeLogQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChangeLogQueryRepositoryImpl implements ChangeLogQueryRepository {

  private final JPAQueryFactory queryFactory;

  private static final QChangeLog c = QChangeLog.changeLog;

}
