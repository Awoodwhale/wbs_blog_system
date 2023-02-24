<template>
    <div id="main-body">
        <main>
            <div class="box">
                <div class="inner-box">
                    <div class="forms-wrap">
                        <form autocomplete="off" class="sign-in-form" @submit.prevent="doLogin">
                            <div class="logo">
                                <img src="/whale.png" alt="woodwhale"/>
                                <h4>woodwhale's blog</h4>
                            </div>

                            <div class="heading">
                                <h2>WBS 博客登录</h2>
                                <h6>还未注册?</h6>
                                <a href="#" class="toggle">点我注册!</a>
                            </div>

                            <div class="actual-form">
                                <div class="input-wrap">
                                    <input
                                        v-model="loginUsername"
                                        type="text"
                                        minlength="4"
                                        maxLength="50"
                                        class="input-field"
                                        autocomplete="off"
                                        required
                                    />
                                    <label>请输入用户名或邮箱</label>
                                </div>

                                <div class="input-wrap">
                                    <input
                                        v-model="loginPasswd"
                                        type="password"
                                        minlength="4"
                                        class="input-field"
                                        autocomplete="off"
                                        required
                                    />
                                    <label>请输入密码</label>
                                </div>

                                <div class="input-wrap" style=" display: grid; grid-template-columns: 62% 38%;">
                                    <div>
                                        <input
                                            v-model="loginCaptcha"
                                            style="width: 60%"
                                            type="text"
                                            minlength="5"
                                            class="input-field"
                                            autocomplete="off"
                                            required
                                        />
                                        <label>请输入验证码</label>
                                    </div>

                                    <el-image style="border: solid 1px #d4d4d4; border-radius: 10px;cursor: pointer;"
                                              :src="captchaPath" @click="updateCaptcha"
                                              class="captcha-code hvr-grow"
                                              fit="contain"/>
                                </div>

                                <input type="submit" value="登录" class="sign-btn"/>

                                <p class="text" style="text-align: center">
                                    不会有人忘记密码了吧？！万事留一手！<br>
                                    <a href="#" style="font-weight: 800">点击这里找回密码！</a>
                                </p>
                            </div>
                        </form>

                        <form autocomplete="off" class="sign-up-form" @submit.prevent="doRegister">
                            <div class="logo">
                                <img src="/whale.png" alt="woodwhale"/>
                                <h4>woodwhale's blog</h4>
                            </div>

                            <div class="heading">
                                <h2>WBS 博客注册</h2>
                                <h6>已有帐号?</h6>
                                <a href="#" class="toggle">点我登录!</a>
                            </div>

                            <div class="actual-form">
                                <div class="input-wrap">
                                    <input
                                        v-model="registerUsername"
                                        type="text"
                                        minlength="4"
                                        maxLength="50"
                                        class="input-field"
                                        autocomplete="off"
                                        required
                                    />
                                    <label>请输入用户名</label>
                                </div>

                                <div class="input-wrap" style=" display: grid; grid-template-columns: 72% 28%;">
                                    <div>
                                        <input
                                            v-model="registerEmail"
                                            style="width: 70%"
                                            type="email"
                                            class="input-field"
                                            autocomplete="off"
                                            required
                                        />
                                        <label>请输入邮箱</label>
                                    </div>
                                    <div>
                                        <input type="button"
                                               id="email-btn"
                                               @click="getEmailCaptcha"
                                               style="height: 37px;margin-bottom: 0;"
                                               class="sign-btn" value="验证码">
                                    </div>

                                </div>

                                <div class="input-wrap">
                                    <input
                                        v-model="registerCaptcha"
                                        type="text"
                                        minlength="6"
                                        maxLength="6"
                                        class="input-field"
                                        autocomplete="off"
                                        required
                                    />
                                    <label>请输入邮箱验证码</label>
                                </div>

                                <div class="input-wrap">
                                    <input
                                        v-model="registerPasswd"
                                        id="Password"
                                        type="password"
                                        minlength="4"
                                        class="input-field"
                                        autocomplete="off"
                                        required
                                    />
                                    <label>请输入密码</label>
                                </div>

                                <div class="input-wrap">
                                    <input
                                        id="RePassword"
                                        type="password"
                                        minlength="4"
                                        class="input-field"
                                        autocomplete="off"
                                        required
                                        @change="checkPassword()"
                                    />
                                    <label>请确认密码</label>
                                </div>

                                <input type="submit" value="注册" class="sign-btn"/>
                                <p class="text" style="text-align: center">
                                    注册后就可以对博客进行评论啦！
                                </p>
                            </div>
                        </form>
                    </div>

                    <div class="carousel">
                        <div class="images-wrapper">
                            <img src="/tt1.jpg" class="image img-1 show" alt=""/>
                            <img src="/tt2.jpg" class="image img-2" alt=""/>
                            <img src="/tt3.jpg" class="image img-3" alt=""/>
                        </div>

                        <div class="text-slider">
                            <div class="text-wrap">
                                <div class="text-group">
                                    <h2>Welcome to woodwhale's blog</h2>
                                    <h2>You can find valuable articles here</h2>
                                    <h2>Enjoy yourself</h2>
                                </div>
                            </div>

                            <div class="bullets">
                                <span class="active" data-value="1"></span>
                                <span data-value="2"></span>
                                <span data-value="3"></span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    </div>

</template>


<script>
import {getSeoInfos, isEmail, isEmpty, showNotify} from "@/assets/utils/utils";
import md5 from 'js-md5';
import {
    checkUserLogin,
    sendEmailCaptcha,
    showEmailIsRegister,
    SUCCESS,
    userLogin,
    userRegister
} from "@/assets/api/api";

export default {
    layout: 'normal',
    data() {
        return {
            captchaPath: '',
            captchaKey: '',
            loginUsername: '',
            loginPasswd: '',
            loginCaptcha: '',
            registerUsername: '',
            registerEmail: '',
            registerPasswd: '',
            registerCaptcha: '',
            wait: 60,
        }
    },
    methods: {
        updateCaptcha() {
            this.captchaPath = '/user/captcha?captcha_code_key=' + this.captchaKey + "&random=" + Math.random();
        },
        checkPassword() {
            this.$nextTick(() => {
                let pass1 = document.getElementById("Password");
                let pass2 = document.getElementById("RePassword");
                if (pass1.value !== pass2.value)
                    pass2.setCustomValidity("两次输入的密码不匹配");
                else
                    pass2.setCustomValidity("");
            })
        },
        doLogin() {
            if (this.loginUsername !== '' && this.loginPasswd !== '' && this.loginCaptcha !== '') {
                // 发起登录，检查数据
                userLogin(this.loginCaptcha, this.captchaKey, {
                    email: this.loginUsername,
                    username: this.loginUsername,
                    password: md5(this.loginPasswd)
                }).then(data => {
                    if (data.code === 20002) {
                        showNotify("WBS登录", data.message + " 欢迎回来！")
                        checkUserLogin().then(res => {
                            if (res.code === SUCCESS) {
                                this.$store.commit("setUserInfo", res.data)
                            } else {
                                showNotify("WBS用户信息", "用户信息异常，请登录重试！", "error")
                            }
                        })
                        // 跳转到主页
                        this.$router.push("/")
                    } else {
                        showNotify("WBS登录", data.message, 'error')
                        this.updateCaptcha()
                        this.loginCaptcha = ''
                    }
                })
            }
        },
        // 注册
        doRegister() {
            if (!isEmpty(this.registerUsername) && !isEmpty(this.registerEmail) &&
                !isEmpty(this.registerPasswd) && !isEmpty(this.registerCaptcha)) {
                userRegister({
                    email: this.registerEmail,
                    username: this.registerUsername,
                    password: md5(this.registerPasswd)
                }, this.registerCaptcha).then(res => {
                    if (res.code === 20001) {
                        // 注册成功
                        showNotify("WBS注册", res.message + " 请前往登录！")
                        // 刷新当前页面
                        location.reload();
                    } else {
                        showNotify("WBS注册", res.message, 'error')
                    }
                })
            }
        },
        getEmailCaptcha() {
            // 如果不是邮箱或者未空就警告
            if (isEmpty(this.registerEmail) || !isEmail(this.registerEmail)) {
                showNotify("WBS注册", "请输入正确的邮箱格式!", "warning")
                return
            }
            showEmailIsRegister(this.registerEmail).then(res => {
                if (res.code === SUCCESS) {
                    // 已注册
                    showNotify("WBS注册", "该邮箱已被注册，请重试", 'error')
                } else if (res.code === 40000) {
                    // 未注册
                    // 禁用按钮
                    this.time()
                    // 发送验证码
                    sendEmailCaptcha("register", this.registerEmail).then(res => {
                        if (res.code === SUCCESS) {
                            showNotify("WBS注册", res.message)
                        } else {
                            showNotify("WBS注册", res.message, "error")
                        }
                    })
                }
            })
        },
        time() {
            const btn = document.getElementById("email-btn")
            if (this.wait === 0) {
                btn.removeAttribute('disabled')
                btn.value = "验证码"
                this.wait = 60
            } else {
                btn.setAttribute('disabled', 'disabled')
                btn.value = this.wait + "s"
                this.wait--
                setTimeout(() => {
                        this.time()
                    },
                    1000)
            }
        }
    },
    created() {
        this.captchaKey = Date.parse(new Date());
        this.updateCaptcha();
    },
    // 获取必要数据
    async asyncData(ctx) {
        await getSeoInfos(ctx)
    },
    mounted() {
        this.$nextTick(() => {
            const inputs = document.querySelectorAll(".input-field");
            const toggle_btn = document.querySelectorAll(".toggle");
            const main = document.querySelector("main");
            const bullets = document.querySelectorAll(".bullets span");
            const images = document.querySelectorAll(".image");

            inputs.forEach((inp) => {
                inp.addEventListener("focus", () => {
                    inp.classList.add("active");
                });
                inp.addEventListener("blur", () => {
                    if (inp.value !== "") return;
                    inp.classList.remove("active");
                });
            });

            toggle_btn.forEach((btn) => {
                btn.addEventListener("click", () => {
                    main.classList.toggle("sign-up-mode");
                });
            });

            function moveSlider() {
                let index = this.dataset.value;

                let currentImage = document.querySelector(`.img-${index}`);
                images.forEach((img) => img.classList.remove("show"));
                currentImage.classList.add("show");

                const textSlider = document.querySelector(".text-group");
                textSlider.style.transform = `translateY(${-(index - 1) * 2.2}rem)`;

                bullets.forEach((bull) => bull.classList.remove("active"));
                this.classList.add("active");
            }

            bullets.forEach((bullet) => {
                bullet.addEventListener("click", moveSlider);
            });

        })
    }
}
</script>


<style scoped>

/deep/ .el-image__placeholder {
    background: url("/icon_loading.png") center center no-repeat;
    background-size: auto 50%;
    border: solid 1px #d4d4d4;
}

#main-body,
#main-body::before,
#main-body::after {
    padding: 0;
    margin: 0;
    box-sizing: border-box;
}

main {
    width: 100%;
    min-height: 100vh;
    overflow: hidden;
    /*background-color: #ff8c6b;*/
    padding: 2rem;
    display: flex;
    align-items: center;
    justify-content: center;
}

.box {
    position: relative;
    width: 100%;
    max-width: 1020px;
    height: 640px;
    background-color: #fff;
    border-radius: 3.3rem;
    box-shadow: 0 60px 40px -30px rgba(0, 0, 0, 0.27);
}

.inner-box {
    position: absolute;
    width: calc(100% - 4.1rem);
    height: calc(100% - 4.1rem);
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
}

.forms-wrap {
    position: absolute;
    height: 100%;
    width: 45%;
    top: 0;
    left: 0;
    display: grid;
    grid-template-columns: 1fr;
    grid-template-rows: 1fr;
    transition: 0.8s ease-in-out;
}

form {
    max-width: 260px;
    width: 100%;
    margin: 0 auto;
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: space-evenly;
    grid-column: 1 / 2;
    grid-row: 1 / 2;
    transition: opacity 0.02s 0.4s;
}

form.sign-up-form {
    opacity: 0;
    pointer-events: none;
}

.logo {
    display: flex;
    align-items: center;
}

.logo img {
    width: 27px;
    margin-right: 0.3rem;
}

.logo h4 {
    font-size: 1.2rem;
    margin-top: -3px;
    letter-spacing: -0.5px;
    color: #151111;
}

.heading h2 {
    font-size: 2.2rem;
    font-weight: 600;
    color: #151111;
}

.heading h6 {
    color: #bababa;
    font-weight: 400;
    font-size: 0.85rem;
    display: inline;
}

.toggle {
    color: #151111;
    text-decoration: none;
    font-size: 0.95rem;
    font-weight: 1000;
    transition: 0.3s;
}

.toggle:hover {
    color: #8371fd;
}

.input-wrap {
    position: relative;
    height: 37px;
    margin-bottom: 2rem;
}

.input-field {
    position: absolute;
    width: 100%;
    height: 100%;
    background: none;
    border: none;
    outline: none;
    border-bottom: 1px solid #bbb;
    padding: 0;
    font-size: 0.95rem;
    color: #151111;
    transition: 0.4s;
}

label {
    position: absolute;
    left: 0;
    top: 50%;
    transform: translateY(-50%);
    font-size: 0.95rem;
    color: #bbb;
    pointer-events: none;
    transition: 0.4s;
}

.input-field.active {
    border-bottom-color: #151111;
}

.input-field.active + label {
    font-size: 0.75rem;
    top: -2px;
}

.sign-btn {
    display: inline-block;
    width: 100%;
    height: 43px;
    background-color: #151111;
    color: #fff;
    border: none;
    cursor: pointer;
    border-radius: 0.8rem;
    font-size: 0.8rem;
    margin-bottom: 1.2rem;
    transition: 0.3s;
}

.sign-btn:hover {
    background-color: #8371fd;
}

.text {
    color: #bbb;
    font-size: 0.7rem;
}

.text a {
    color: #bbb;
    transition: 0.3s;
}

.text a:hover {
    color: #8371fd;
}

main.sign-up-mode form.sign-in-form {
    opacity: 0;
    pointer-events: none;
}

main.sign-up-mode form.sign-up-form {
    opacity: 1;
    pointer-events: all;
}

main.sign-up-mode .forms-wrap {
    left: 55%;
}

main.sign-up-mode .carousel {
    left: 0%;
}

.carousel {
    position: absolute;
    height: 100%;
    width: 55%;
    left: 45%;
    top: 0;
    /*background-color: #ffe0d2;*/
    background-color: #eefff8;
    border-radius: 2rem;
    display: grid;
    grid-template-rows: auto 1fr;
    /*padding-bottom: 2rem;*/
    overflow: hidden;
    transition: 0.8s ease-in-out;
}

.images-wrapper {
    display: grid;
    grid-template-columns: 1fr;
    grid-template-rows: 1fr;
}

.image {
    width: 100%;
    height: 450px;
    grid-column: 1/2;
    grid-row: 1/2;
    opacity: 0;
    transition: opacity 0.3s, transform 0.5s;
}

.img-1 {
    transform: translate(0, -50px);
}

.img-2 {
    transform: scale(0.4, 0.5);
}

.img-3 {
    transform: scale(0.3) rotate(-20deg);
}

.image.show {
    opacity: 1;
    transform: none;
}

.text-slider {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
}

.text-wrap {
    max-height: 2.2rem;
    overflow: hidden;
    margin-bottom: 1.2rem;
}

.text-group {
    display: flex;
    flex-direction: column;
    text-align: center;
    transform: translateY(0);
    transition: 0.5s;
}

.text-group h2 {
    line-height: 2.2rem;
    font-weight: 600;
    font-size: 1.6rem;
    font-family: Comic Sans MS, Comic Sans, cursive;
}

.bullets {
    display: flex;
    align-items: center;
    justify-content: center;
}

.bullets span {
    display: block;
    width: 0.5rem;
    height: 0.5rem;
    background-color: #aaa;
    margin: 0 0.25rem;
    border-radius: 50%;
    cursor: pointer;
    transition: 0.3s;
}

.bullets span.active {
    width: 1.1rem;
    background-color: #151111;
    border-radius: 1rem;
}

@media (max-width: 850px) {
    .box {
        height: auto;
        max-width: 550px;
        overflow: hidden;
    }

    .inner-box {
        position: static;
        transform: none;
        width: revert;
        height: revert;
        padding: 2rem;
    }

    .forms-wrap {
        position: revert;
        width: 100%;
        height: auto;
    }

    form {
        max-width: revert;
        padding: 1.5rem 2.5rem 2rem;
        transition: transform 0.8s ease-in-out, opacity 0.45s linear;
    }

    .heading {
        margin: 2rem 0;
    }

    form.sign-up-form {
        transform: translateX(100%);
    }

    main.sign-up-mode form.sign-in-form {
        transform: translateX(-100%);
    }

    main.sign-up-mode form.sign-up-form {
        transform: translateX(0%);
    }

    .carousel {
        position: revert;
        height: auto;
        width: 100%;
        padding: 3rem 2rem;
        display: flex;
    }

    .images-wrapper {
        display: none;
    }

    .text-slider {
        width: 100%;
    }
}

@media (max-width: 530px) {
    main {
        padding: 1rem;
    }

    .box {
        border-radius: 2rem;
    }

    .inner-box {
        padding: 1rem;
    }

    .carousel {
        padding: 1.5rem 1rem;
        border-radius: 1.6rem;
    }

    .text-wrap {
        margin-bottom: 1rem;
    }

    .text-group h2 {
        font-size: 1.2rem;
    }

    form {
        padding: 1rem 2rem 1.5rem;
    }
}

</style>
