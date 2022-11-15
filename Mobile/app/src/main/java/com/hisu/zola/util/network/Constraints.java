package com.hisu.zola.util.network;

public interface Constraints {
//  CONTENT TYPE
    String JSON_TYPE = "application/json";
    String MULTIPART_FORM_DATA_TYPE = "multipart/form-data";

//  API
    String API_LOGIN = "api/auth/signin";
    String API_REGISTER = "api/auth/signup";
    String API_CHANGE_PASSWORD = "api/auth/updatePassword";
    String API_CHANGE_PHONE_NUMBER = "api/auth/updatePhonenumber";
    String API_CHECK_USER_BY_PHONE_NUMBER = "api/auth/checkPhonenumber";

    String API_GET_ALL_FRIENDS = "api/user/getAllFriends";
    String API_UPDATE_PROFILE = "api/user/updateProfile";
    String API_SEND_FRIEND_REQUEST = "api/user/requestAddFriend";
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
    String API_ADD_GROUP_MEMBER= "api/conversation/addMemberGroup";
    String API_REMOVE_GROUP_MEMBER= "api/conversation/deleteMember";
    String API_DISBAND_GROUP = "api/conversation/deleteGroup";
    String API_OUT_GROUP = "api/conversation/outGroup";

//  Todo: SOCKET EVENT
}