package com.duang.easyecard.GlobalData;

public class UrlConstant {

    /**
     * “掌上校园”网页版网址
     */
    // 主页（Host)
    public final static String MOBILE_INDEX = "http://card.ouc.edu.cn:8070";
    // 登录
    public final static String MOBILE_LOGIN = MOBILE_INDEX + "/Account/Login";
    // 基本信息
    public final static String MOBILE_MANAGE_BASIC_INFO = MOBILE_INDEX + "/SynCard/Manage/BasicInfo";
    /**
     * 流水查询
     */
    // 当日流水
    public final static String MOBILE_MANAGE_CURRENT_DAY_TRJN = MOBILE_INDEX +
            "/SynCard/Manage/CurrentDayTrjn";
    // 一周流水
    public final static String MOBILE_MANAGE_ONE_WEEK_TRJN = MOBILE_INDEX +
            "/SynCard/Manage/OneWeekTrjn";
    // 历史流水
    // public final static String MOBILE_MANAGE_HISTORY_TRJN = MOBILE_INDEX;
    // 校园卡挂失
    public final static String MOBILE_MANAGE_CARD_LOST = MOBILE_INDEX +
            "/SynCard/Manage/CardLost";
    // 修改密码
    public final static String MOBILE_MANAGE_CHANGE_QUERY_PWD = MOBILE_INDEX +
            "/SynCard/Manage/ChangeQueryPwd";
    // 常见问题，校园卡管理
    public final static String MOBILE_FAQ_XYKGL = MOBILE_INDEX +
            "/InfoPub/Notice/List/?sysCode=Dreams&typeCode=XYKGL";
    // 常见问题，应用中心
    public final static String MOBILE_FAQ_YYZX = MOBILE_INDEX +
            "/InfoPub/Notice/List/?sysCode=Dreams&typeCode=YYZX";
    // 常见问题，帐户安全
    public final static String MOBILE_FAQ_ZHAQ = MOBILE_INDEX +
            "/InfoPub/Notice/List/?sysCode=Dreams&typeCode=ZHAQ";
    // 常见问题，在线缴费
    public final static String MOBILE_FAQ_ZXJF = MOBILE_INDEX +
            "/InfoPub/Notice/List/?sysCode=Dreams&typeCode=ZXJF";

    /**
     * 校园卡电子服务平台网址
     */
    // 主页
    public final static String INDEX = "http://card.ouc.edu.cn";
    // MINI登录界面，POST登录信息
    public final static String MINI_CHECK_IN = INDEX + "/Account/MiniCheckIn";
    // 注销
    // public final static String SIGN_OFF = "http://card.ouc.edu.cn:8050/Account/SignOff";
    // 验证码地址
    public final static String GET_CHECKCODE_IMG = INDEX + "/Account/GetCheckCodeImg/Flag=0";
    // 基本信息
    public final static String BASIC_INFO = INDEX + "/CardManage/CardInfo/BasicInfo";
    // 流水查询,先POST这个地址再进行查询
    public final static String TRJN_QUERY = INDEX + "/CardManage/CardInfo/TrjnQuery";
    public static String trjnListStartTime;  // 历史流水查询起始时间
    public static String trjnListEndTime;  // 历史流水查询结束
    public static int historyTrjnListPageIndex;  // 历史流水查询页码
    public static int todayTrjnListPageIndex;  // 当日流水查询页码

    // 获取当日查询网址
    public static String getTrjnListToday() {
        return INDEX + "/CardManage/CardInfo/TrjnList?type=0&_=0&pageindex="
                + todayTrjnListPageIndex;
    }

    // 获得历史流水查询网址
    public static String getTrjnListHistory() {
        return INDEX + "/CardManage/CardInfo/TrjnList?beginTime=" + trjnListStartTime + "&endTime="
                + trjnListEndTime + "&type=1&_=0&pageindex=" + historyTrjnListPageIndex
                + "&X-Requested-With=XMLHttpRequest";
    }

    // 校园卡挂失
    public final static String LOSS_CARD = INDEX + "/CardManage/CardInfo/LossCard?_=0";
    public final static String LOSS_GET_NUM_KEY_PAD_IMG = INDEX + "/Account/GetNumKeyPadImg";
    public final static String LOSS_GET_CHECKCODE_IMG = INDEX + "/Account/GetCheckCodeImg?rad=0";
    public final static String SET_CARD_LOST = INDEX + "/CardManage/CardInfo/SetCardLost";

    // 应用中心
    public final static String MANAGEMENT = INDEX + "/Backend/Management/Index";

    // 失卡招领信息浏览页码
    public static int cardLossPageIndex = 1;  // 失卡信息查询页码

    // 获得失卡招领信息网址
    public static String getCardLossInfoBrowsing() {
        return INDEX + "/CardManage/CardLoss/ManageIndex?_=0&pageindex=" + cardLossPageIndex +
                "&X-Requested-With=XMLHttpRequest";
    }

    // 失卡招领详细信息ID
    public static int cardLossViewDetailId;

    // 获得失卡招领详细信息网址
    public static String getCardLossInfoViewDetail() {
        return INDEX + "/CardManage/CardLoss/Detail/" + cardLossViewDetailId + "?_=0";
    }

    // 丢失卡登记GET
    public static String CARD_LOSS_LOST_MY_CARD = INDEX + "/CardManage/CardLoss/LostMyCard?_=0";
    // 丢失卡登记POST
    public static String CARD_LOSS_LOSE = INDEX + "/CardManage/CardLoss/Lose";
    // 失卡招领信息搜索，用于确定事件id (POST)
    public static String CARD_LOSS_INFO_MANAGELIST = INDEX + "/CardManage/CardLoss/ManageList";
    // 丢失卡找到POST，需要在后面+id;
    public static String CARD_LOSS_PICK_UP_CARD = INDEX + "/CardManage/CardLoss/PickUpCard/";


    // 信息中心主页，搜索信息前先访问这个界面，以获得响应cookie
    public static String NOTICE_INDEX = INDEX + "/Notice/Main/MyNoticeIndex?_=0";

    // 获得未读消息数目
    public static String getAllMyUnreadCount(String id) {
        return INDEX + "/Notice/Main/GetAllMyUnReadCount?sysUserID=" + id + "&_=0";
    }

    // 收件箱
    public static int receivedNoticeIndex = 1;

    // 获得收件箱消息网址
    public static String getReceivedNotice() {
        return INDEX + "/Notice/Main/MyReceiveNotice?_=0&pageindex=" + receivedNoticeIndex +
                "&X-Requested-With=XMLHttpRequest";
    }

    // 已发送
    public static int sentNoticeIndex = 1;

    // 获得已发送消息网址
    public static String getSentNotice() {
        return INDEX + "/Notice/Main/MySendNotice?_=0&pageindex=" + sentNoticeIndex +
                "&X-Requested-With=XMLHttpRequest";
    }

    // 详细消息
    public static String NOTICE_DETAIL = INDEX + "/Notice/Main/PersonalDetail";

    // 发送消息
    public static String CREATE_NOTICE = INDEX + "/Notice/Main/CreateNotice";
    // 查找用户
    public static String QUERY_USER = INDEX + "/Notice/Main/QueryUser";

    // 删除消息
    public static String DELETE_NOTICE = INDEX + "/Notice/Main/DeleteMyNotice";

    // 留言板
    public static String MESSAGE_BOARD = INDEX + "/Communication/MsgBoard/ManageIndex?_=0";

    // 用户个人信息
    public static String USER_INFO = INDEX + "/Account/UserInfo?_=0";

    // 从一卡通同步信息
    public static String ONE_KEY_SYNC = INDEX + "/Account/OneKeySynchron";

    // 修改个人信息
    public static String EDIT_USER_INFO = INDEX + "/Account/EUserInfo";
}
