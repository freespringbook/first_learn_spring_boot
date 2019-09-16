package com.community.batch.jobs.inactive;

import com.community.batch.domain.enums.Grade;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by freejava1191@gmail.com on 2019-09-16
 * Blog : https://freedeveloper.tistory.com/
 * GitHub : https://github.com/freelife1191
 *
 * 회원 등급에 따라 파티션을 분할하는 InactiveUserPartitioner
 */
public class InactiveUserPartitioner implements Partitioner {

    private static final String GRADE = "grade";
    private static final String INACTIVE_USER_TASK = "InactiveUserTask";

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        // gridSize만큼 Map 크기를 할당함
        Map<String, ExecutionContext> map = new HashMap<>(gridSize);
        // Grade Enum에 정의된 모든 값을 grades 배열 변수로 할당함
        Grade[] grades = Grade.values();
        // grades 값만큼 파티션을 생성하는 루프문을 돌림
        for(int i = 0, length = grades.length; i < length; i++) {
            ExecutionContext context = new ExecutionContext();
            // Step에서 파라미터로 Grade 값을 받아서 사용함
            // 이때 ExecutionContext 키값은 'grade' 임
            // Grade Enum의 이름값을 context에 추가함
            context.putString(GRADE, grades[i].name());
            // 반환되는 map에 'inactiveUserTask1..2..3' 형식의 파티션 키값을 지정하고 위에서 추가한 ExecutionContext를 map에 추가함
            map.put(INACTIVE_USER_TASK + i, context);
        }
        return map;
    }
}
