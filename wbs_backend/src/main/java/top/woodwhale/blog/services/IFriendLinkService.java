package top.woodwhale.blog.services;

import top.woodwhale.blog.pojo.FriendLink;
import top.woodwhale.blog.response.ResponseResult;

public interface IFriendLinkService {
    ResponseResult addFriendLink(FriendLink friendLink);

    ResponseResult deleteFriendLink(String friendLinkId);

    ResponseResult updateFriendLink(String friendLinkId, FriendLink friendLinkInfo);

    ResponseResult getFriendLink(String friendLinkId);

    ResponseResult listFriendLinks();

}
