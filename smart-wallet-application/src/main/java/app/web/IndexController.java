package app.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController {

  public ModelAndView index() {

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("index");

    return modelAndView;
  }
}
