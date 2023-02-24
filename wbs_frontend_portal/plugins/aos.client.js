import 'aos/dist/aos.css'
import AOS from 'aos/dist/aos.js'

export default ({
  app
}) => {
  app.AOS = new AOS.init({
    disable: 'mobile' ,
    disableMutationObserver: false,
    debounceDelay: 50,
    throttleDelay: 99,
  });
};
