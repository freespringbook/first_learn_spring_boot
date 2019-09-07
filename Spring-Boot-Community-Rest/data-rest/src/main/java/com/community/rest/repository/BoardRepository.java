package com.community.rest.repository;

import com.community.rest.domain.Board;
import com.community.rest.domain.projection.BoardOnlyContainTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * @RepositoryRestResource 별도의 컨트롤러와 서비스 영역 없이 미리 내부적으로 정의되어 있는 로직을 따라 처리됨
 * 그 로직은 해당 도메인의 정보를 매핑하여 REST API를 제공하는 역할을
 */
@RepositoryRestResource(excerptProjection = BoardOnlyContainTitle.class)
public interface BoardRepository extends JpaRepository<Board, Long> {

    /**
     * @PreAuthorize 로 save 메서드에 ADMIN 권한 지정
     * @param entity
     * @param <S>
     * @return
     */
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    <S extends Board> S save(S entity);

    /**
     * 제목을 찾는 쿼리 메서드
     * @RestResource 의 path를 설정하지 않으면 기본값에 해당 메서드명이 적용됨
     * 'query'로 값을 변경하여 path 값을 다르게 줄 수 있음
     * @param title
     * @return
     */
    @RestResource(path = "query")
    List<Board> findByTitle(@Param("title") String title);
}