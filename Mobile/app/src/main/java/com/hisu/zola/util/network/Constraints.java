package com.hisu.zola.util.network;

public interface Constraints {
    //Permission Code
    int STORAGE_PERMISSION_CODE = 100;
    int CONTACT_PERMISSION_CODE = 1;


    //  CONTENT TYPE
    String JSON_TYPE = "application/json";
    String MULTIPART_FORM_DATA_TYPE = "multipart/form-data";
    String FILE_TYPE_GENERAL = "application/";
    String TEXT_TYPE_GENERAL = "text";
    String VIDEO_TYPE_GENERAL = "media/";
    String CALL_TYPE_GENERAL = "call";
    String GROUP_NOTIFICATION_TYPE_GENERAL = "notification";
    String GOOGLE_DOCS_URL = "https://docs.google.com/gview?embedded=true&url=";

    //  API
    String API_LOGIN = "api/auth/signin";
    String API_REGISTER = "api/auth/signup";
    String API_CHANGE_PASSWORD = "api/auth/updatePassword";
    String API_CHANGE_PHONE_NUMBER = "api/auth/updatePhonenumber";
    String API_CHECK_USER_BY_PHONE_NUMBER = "api/auth/checkPhonenumber";

    String API_GET_ALL_FRIENDS = "api/user/getAllFriends";
    String API_UPDATE_PROFILE = "api/user/updateProfile";
    String API_SEND_FRIEND_REQUEST = "api/user/requestAddFriend";
    String API_UN_SEND_FRIEND_REQUEST = "api/user/recallFriend";
    String API_ACCEPT_FRIEND_REQUEST = "api/user/acceptFriend";
    String API_DENY_FRIEND_REQUEST = "api/user/deniedFriend";
    String API_UNFRIEND_REQUEST = "api/user/deleteFriend";
    String API_GET_USER_BY_PHONE_NUMBER = "api/user/getUserByPhonenumber";

    String API_GET_ALL_MESSAGE = "api/message/getAllMessage";
    String API_SEND_MESSAGE = "api/message/sendMessage";
    String API_DELETE_MESSAGE = "api/message/deleteMessage";
    String API_UPLOAD_FILE = "api/message/uploadFile";

    String API_CREATE_CONVERSATION = "api/conversation/createConversation";
    String API_GET_ALL_CONVERSATION_OF_USER = "api/conversation/getAllConversations";
    String API_CHANGE_GROUP_NAME = "api/conversation/changeLabel";
    String API_CHANGE_GROUP_ADMIN = "api/conversation/updateCreator";
    String API_ADD_GROUP_MEMBER = "api/conversation/addMemberGroup";
    String API_REMOVE_GROUP_MEMBER = "api/conversation/deleteMember";
    String API_DISBAND_GROUP = "api/conversation/deleteGroup";
    String API_OUT_GROUP = "api/conversation/outGroup";
    String API_CHECK_GROUP = "api/conversation/checkConversation";

    //  Socket
    String EVT_ADD_MEMBER = "addMemberToGroup";
    String EVT_ADD_MEMBER_RECEIVE = "addMemberToGroup-receive";
    String EVT_REMOVE_MEMBER_RECEIVE = "deleteMemberGroup-receive";
    String EVT_REMOVE_MEMBER_MOBILE_RECEIVE = "deleteMemberGroup-receiveMobile";
    String EVT_CHANGE_GROUP_NAME = "changeGroupName";
    String EVT_CHANGE_GROUP_NAME_RECEIVE = "changeGroupName-receive";
    String EVT_OUT_GROUP = "outGroup";
    String EVT_OUT_GROUP_RECEIVE = "outGroup-receive";
    String EVT_CREATE_GROUP = "addConversation";
    String EVT_CREATE_GROUP_RECEIVE = "addConversation-receive";
    String EVT_CHANGE_GROUP_ADMIN = "changeCreatorGroup";
    String EVT_CHANGE_GROUP_ADMIN_RECEIVE = "changeCreatorGroup-receive";
    String EVT_DELETE_GROUP = "deleteGroup";
    String EVT_DELETE_GROUP_RECEIVE = "deleteGroup-receive";

    String EVT_MESSAGE_SEND = "send-msg";
    String EVT_MESSAGE_RECEIVE = "msg-receive";
    String EVT_DELETE_MESSAGE = "delete-msg";
    String EVT_DELETE_MESSAGE_RECEIVE = "delete-receive";
    String EVT_ON_TYPING = "onTypingText";
    String EVT_ON_TYPING_RECEIVE = "onTypingTextToClient";
    String EVT_OFF_TYPING = "offTypingText";
    String EVT_OFF_TYPING_RECEIVE = "offTypingTextToClient";

    String EVT_ADD_FRIEND = "requestAddFriend";
    String EVT_ADD_FRIEND_RECEIVE = "requestAddFriendToClient";
    String EVT_ACCEPT_FRIEND_REQUEST = "acceptAddFriend";
    String EVT_ACCEPT_FRIEND_REQUEST_RECEIVE = "acceptAddFriendToClient";
    String EVT_UNSENT_FRIEND_REQUEST = "recallFriend";
    String EVT_UNSENT_FRIEND_REQUEST_RECEIVE = "recallFriendToClient";
    String EVT_DELETE_FRIEND = "deleteFriend";
    String EVT_DELETE_FRIEND_RECEIVE = "deleteFriendToClient";
}