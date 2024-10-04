package com.jiraynor.boardback.dto.response;

import com.jiraynor.boardback.common.ResponseCode;
import com.jiraynor.boardback.common.ResponseMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter // 각 필드에 대해 getter 메서드를 자동으로 생성해줌.
@AllArgsConstructor // 모든 필드를 매개변수로 받는 생성자를 자동으로 생성해줌.
public class ResponseDto { // 서버에서 응답으로 보낼 데이터를 담는 객체.
    private String code; // 응답 상태 코드를 저장하는 필드.
    private String message; // 응답 메시지를 저장하는 필드.

    // 데이터베이스 오류에 대한 응답을 생성하는 정적 메서드
    public static ResponseEntity<ResponseDto> databaseError() {
        // ResponseDto 객체를 생성하고 응답 코드와 메시지를 설정.
        ResponseDto responseBody = new ResponseDto(
                ResponseCode.DATABASE_ERROR, // DATABASE_ERROR 코드를 사용 (DB 오류가 발생한 상황).
                ResponseMessage.DATABASE_ERROR // DATABASE_ERROR 메시지를 사용 (DB 오류 메시지).
        );

        // HTTP 상태 코드를 INTERNAL_SERVER_ERROR(500)로 설정하고, 응답 본문을 설정하여 ResponseEntity를 반환.
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR) // HTTP 상태 코드를 500(서버 내부 오류)로 설정.
                .body(responseBody); // 응답 본문에 ResponseDto 객체를 담아서 반환.
    }
}
