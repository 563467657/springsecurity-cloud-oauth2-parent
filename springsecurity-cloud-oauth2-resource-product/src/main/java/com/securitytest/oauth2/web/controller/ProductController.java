package com.securitytest.oauth2.web.controller;

import com.securitytest.base.result.JsonResult;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    
    @GetMapping("/list")
    //@PreAuthorize("hasAuthority('sys:user:list')")
    public JsonResult list() {
        List<String> list = new ArrayList<>();
        list.add("眼镜");
        list.add("格子衬衫");
        list.add("双肩包");
        return JsonResult.ok(list);
    }

}
