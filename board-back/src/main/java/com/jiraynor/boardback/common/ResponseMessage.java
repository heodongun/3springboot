package com.jiraynor.boardback.common;

public interface ResponseMessage {
    //HTTP status 200
    String SUCCESS="Success.";//인터페이스는 이걸로 쓰는게 맞다고한다 지워도 자동임

    //HTTP Status 400
    String VALIDATION="Validation Failed.";
    String DUPLICATE_EMAIL="Duplicate email.";
    String DUPLICATE_NICKNAME="Duplicate nickname.";
    String DUPLICATE_TEL_NUMBER="Duplicate tel number.";
    String NOT_EXISTED_USER="This user does not exist.";
    String NOT_EXISTED_BOARD="This board does not exist.";

    //http status 401
    String SIGN_IN_FAIL="Login information mismatch.";
    String AUTHORIZATION_FAIL="Authorization Failed.";

    //http status 403
    String NO_PERMISSION="Do not have permission.";

    //http status 500
    String DATABASE_ERROR="Database error.";
}
