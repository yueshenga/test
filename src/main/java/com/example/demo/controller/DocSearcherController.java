package com.example.demo.controller;

import com.example.demo.searcher.DocSearcher;
import com.example.demo.searcher.Result;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DocSearcherController {
    private static final DocSearcher searcher = new DocSearcher();
    private final ObjectMapper objectMapper = new ObjectMapper();


    @RequestMapping(value = "/search",produces = "application/json;charset=utf-8")
    public String search(@RequestParam("query") String query) throws JsonProcessingException {
        //参数是查询词，返回是响应内容
        //参数的query 来自请求的url，querystring的query的值
        List<Result> results = searcher.search(query);
        return objectMapper.writeValueAsString(results);
    }
}

