package com.community.rest.controller;

import com.community.rest.domain.Board;
import com.community.rest.repository.BoardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.PagedResources.PageMetadata;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Slf4j
@RepositoryRestController
@RequestMapping("/boards")
public class BoardRestController {

    private BoardRepository boardRepository;

    // @Autowire와 똑같이 의존성을 주입하는 생성자 주입 방식임
    public BoardRestController(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    /**
     * '/api/boards'로 스프링 부트 데이터 레스트에서 기본적으로 제공해주는 URL 형식을 오버라이드 함
     * @param pageable
     * @return
     */
    @GetMapping
    @ResponseBody
    public Resources<Board> simpleBoard(@PageableDefault Pageable pageable) {
        Page<Board> boardList = boardRepository.findAll(pageable);
        // 전체 페이지 수, 현재 페이지 번호, 총 게시판 수 등의 페이지 정보를 담는 PageMetadata 객체를 생성함
        PageMetadata pageMetadata = new PageMetadata(pageable.getPageSize(), boardList.getNumber(), boardList.getTotalElements());
        // 컬렉션의 페이지 리소스 정보를 추가적으로 제공해주는 PagedResources 객체를 만들어 반환값으로 사용함
        PagedResources<Board> resources = new PagedResources<>(boardList.getContent(), pageMetadata);
        // 필요한 링크를 추가함
        // 사실 여러 링크를 추가하는 것은 상당히 반복적인 작업임
        // 요청된 각각의 Board를 나타내는 'self' 하나만 임시로 추가
        resources.add(linkTo(methodOn(BoardRestController.class).simpleBoard(pageable)).withSelfRel());
        return resources;
    }

}
