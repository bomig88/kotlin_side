package bomi.kotlinside

object GlobalDefine {
    //---- Intent Parameter -----
    const val PARAM_1 = "PARAM1"
    const val PARAM_2 = "PARAM2"
    const val PARAM_3 = "PARAM3"
    const val PARAM_4 = "PARAM4"
    const val PARAM_5 = "PARAM5"
    //---------------------------

    //---- define ----
    const val FINISH_INTERVAL_TIME: Long = 2000 //홈화면 '뒤로' 메뉴로 앱 종료 판단 기준
    //----------------

    //---- Activity Request Code for startActivity with reqCode ----
    const val REQ_CODE_NETWORK_ON = 900 //네트워크 OFF 상태 후 네트워크 ON 유도 체크
    const val PLAY_SERVICES_RESOLUTION_REQUEST = 9000
    //--------------------------------------------------------------

}