package jpabook.jpashop.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class HomeController {

   // Logger log= LoggerFactory.getLogger(getClass());  //slf4j logger사용   ->lombok 어노테이션으로

    @RequestMapping("/")
    public String home()
    {
        log.info("home controller");
        return "home";
    }


}
