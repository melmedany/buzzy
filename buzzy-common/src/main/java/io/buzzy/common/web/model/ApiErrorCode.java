package io.buzzy.common.web.model;

public enum ApiErrorCode {
    Forbidden,
    Unauthorized,
    InvalidScope,
    UsernameNotFound,
    UsernameAlreadyExists,
    UsernameOrPasswordIncorrect,
    ProfileNotFound,
    ConnectionAlreadyExists,
    ConversationNotFound,
    ConversationMessageNotFound,
    Unknown
}
