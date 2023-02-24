<h1 align="center">Welcome to wbs_blog_system 🐋</h1>

> 一个前后端分离的博客系统，后端使用SpringBoot，前端后台管理使用Vue3，前端门户展示使用Nuxt2

## 项目简介

一个前后端分离的多用户博客系统。后端使用SpringBoot，前端后台管理使用Vue3，前端门户展示使用Nuxt2。

目前仅支持多个管理员用户发表文章。普通用户仅有查看文章、评论文章的权力。游客可以查看文章但不能对文章进行评论。

若要部署项目，请阅读每个文件夹中的`README.md`

## 技术栈

### 后端

- SpringBoot 2.6.4
- MySQL 5.7
- Redis 7.0.1
- jsonwebtoken
- solr 8.8
- swagger 3.0
- easy-captcha
- gson

### 前端后台管理

- Vite3
- Vue3
- element-plus
- axios
- v-md-editor
- vue-image-crop-upload

### 前端门户展示

- Nuxt2
- axios
- element-ui
- v-md-editor
- v-viewer
- v-emoji-picker

## 项目展示

### 后台展示

#### 1.管理员登录

后台管理员登录

![admin_login](/imgs/admin_login.png)

#### 2.后台仪表盘

仪表盘展示

![admin_home](/imgs/admin_home.png)

#### 3.文章管理

发布文章

![admin_artical_add](/imgs/admin_artical_add.png)

文章编辑、发布、删除

![admin_article_manager](/imgs/admin_article_manager.png)

草稿编辑、发布、删除

![admin_article_sketch](/imgs/admin_article_sketch.png)

#### 4.日记管理

日记发布、编辑、删除

![admin_diary](/imgs/admin_diary.png)

#### 5.评论管理

评论查看、回复、删除

![admin_comment](/imgs/admin_comment.png)

#### 6.图片管理

图片上传、删除（支持批量操作）

![admin_img_manager](/imgs/admin_img_manager.png)

#### 7.运营管理

文章分类管理

![admin_category_tag](/imgs/admin_category_tag.png)

文章标签管理

![admin_tag_manager](/imgs/admin_tag_manager.png)

#### 8.用户管理

管理员可以设置用户的信息，注销用户等操作

![admin_user_manager](/imgs/admin_user_manager.png)

管理员可以单独编辑自己的信息

![admin_self_menager](/imgs/admin_self_menager.png)

#### 9.SEO

管理员可以设置门户站点SEO信息

![admin_seo_menager](/imgs/admin_seo_menager.png)

#### 10.友链管理

添加、删除友联

![admin_friends_manager](/imgs/admin_friends_manager.png)

### 门户展示

#### 1.用户注册与登录

用户登录

![portal_login](/imgs/portal_login.png)

用户注册

![portal_register](/imgs/portal_register.png)

用户个人信息设置（登录成功后）

![portal_user_manager](/imgs/portal_user_manager.png)

#### 2.首页展示

首页欢迎图

![portal_home](/imgs/portal_home.png)

首页文章展示

![portal_home_articles](/imgs/portal_home_articles.png)

#### 3.文章显示

点击文章查看详情

![portal_article_show](/imgs/portal_article_show.png)

文章归档

![portal_article_list](/imgs/portal_article_list.png)

#### 4.分类与标签

分类和标签的显示

![portal_tags](/imgs/portal_tags.png)

点击具体标签查看该标签下的文章

![portal_tags_list](/imgs/portal_tags_list.png)

#### 5.日记展示

展示日记

![portal_diarys](/imgs/portal_diarys.png)

#### 6.友链展示

展示友链

![portal_friends](/imgs/portal_friends.png)

#### 7.搜索功能

点击搜索或者回车搜索

![portal_search_show](/imgs/portal_search_show.png)

搜索结果（solr支持高亮显示）

![portal_search_result](/imgs/portal_search_result.png)

## 项目作者

**woodwhale**

- Website: https://www.woodwhale.top/
- Github: [@Awoodwhale](https://github.com/Awoodwhale)

## 支持一下

觉得项目不错的话可以点个⭐️！