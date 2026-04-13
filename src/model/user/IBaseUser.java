package model.user;

import java.time.LocalDateTime;

public interface IBaseUser {

    int getUserId();

    String getName();

    String getEmail();

    String getOauthProvider();

    LocalDateTime getCreatedAt();

    boolean isAdmin();
}
