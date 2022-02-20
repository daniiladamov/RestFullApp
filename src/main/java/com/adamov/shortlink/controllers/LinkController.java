package com.adamov.shortlink.controllers;

import com.adamov.shortlink.entity.Link;
import com.adamov.shortlink.services.LinkService;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpClient;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class LinkController {
    private final LinkService linkService;
    private final ExecutorService executorService= Executors.newCachedThreadPool();


    public LinkController(LinkService linkService) {
        this.linkService = linkService;

    }

    @PostMapping("/")
    public ResponseEntity createLink(@RequestBody Map<String, String> param){
if (param.get("url")==null)
    return ResponseEntity.badRequest().body("url must be not empty");
else {
        linkService.createLink(param);
        if (param.get("time")!=null){

            executorService.execute(deleteAfterTime(param,linkService.getShortLink(param.get("url"))));
       }
    return ResponseEntity.ok("link is created");
}
    }

    @GetMapping("/{shortLink}")
    public ResponseEntity redirect(@PathVariable String shortLink){
        Link link=linkService.findLink(shortLink);
        String fullLink=link.getFullLink();
        if (fullLink==null) {return  ResponseEntity.notFound().build();}
        HttpHeaders headers=new HttpHeaders();
        headers.add("Location", fullLink);
        return new ResponseEntity<String>(headers, HttpStatus.FOUND);

    }

    @DeleteMapping("/delete/{shortLink}")
    public ResponseEntity delete(
//            @RequestParam(value = "user_id", required = false) Long id,
//                                 @RequestParam(value = "short_link", required = false)String shortLink
//            @RequestBody Map<String, String> map
    @PathVariable String shortLink){
        if (linkService.delete(shortLink,(long)2))
            return ResponseEntity.ok("short link was deleted");
        else return ResponseEntity.badRequest().body("short link cannot be delete");
    }

    private Runnable deleteAfterTime(Map<String, String> map, String shortLink){
        return new Runnable() {
            @Override
            public void run() {
                RestTemplate restTemplate=new RestTemplate();

                map.put("short_link",shortLink);
                try {
                    Thread.sleep(Long.valueOf(map.get("time")));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                restTemplate.delete("http://localhost/delete/{shortLink}", shortLink);
            }
        };
    }
}
