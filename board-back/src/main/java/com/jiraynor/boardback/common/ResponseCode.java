package com.jiraynor.boardback.common;

public interface ResponseCode {
    //HTTP status 200
    String SUCCESS="SU";//인터페이스는 이걸로 쓰는게 맞다고한다 지워도 자동임

    //HTTP Status 400
    String VALIDATION="VF";
    String DUPLICATE_EMAIL="DF";
    String DUPLICATE_NICKNAME="DN";
    String DUPLICATE_TEL_NUMBER="DT";
    String NOT_EXISTED_USER="NU";
    String NOT_EXISTED_BOARD="NB";

    //http status 401
    String SIGN_IN_FAIL="SF";
    String AUTHORIZATION_FAIL="AF";

    //http status 403
    String NO_PERMISSION="NP";

    //http status 500
    String DATABASE_ERROR="DBE";
}
