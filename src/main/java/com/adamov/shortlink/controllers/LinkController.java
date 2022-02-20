package com.adamov.shortlink.controllers;

import com.adamov.shortlink.entity.Link;
import com.adamov.shortlink.services.LinkService;
import com.adamov.shortlink.statistic.StatisticOfClick;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class LinkController {
    private final LinkService linkService;
    private final StatisticOfClick statistic;


    public LinkController(LinkService linkService, StatisticOfClick statistic) {
        this.linkService = linkService;

        this.statistic = statistic;
    }

    @PostMapping("/")
    public ResponseEntity createLink(@RequestBody Map<String, String> param){
if (param.get("url")==null)
    return ResponseEntity.badRequest().body("url must be not empty");
else {
        if (!linkService.createLink(param))
            return ResponseEntity.badRequest().body("short link was created earlier");
        if (param.get("time")!=null){
            ExecutorService executorService= Executors.newCachedThreadPool();
            executorService.execute(deleteAfterTime(param,linkService.getShortLink(param.get("url"))));}
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
        statistic.addClick(shortLink);
        return new ResponseEntity<String>(headers, HttpStatus.FOUND);

    }

    @GetMapping("/statistics")
    public ResponseEntity statistics(){
        return ResponseEntity.ok(statistic.getMap());
    }

    @DeleteMapping("/delete")
    public ResponseEntity delete(@RequestParam(name="short_link") String shortLink, @RequestParam(name = "user_id") Long id){
            if (linkService.delete(shortLink,id))
            return ResponseEntity.ok("short link was deleted");
        else return ResponseEntity.badRequest().body("short link are not able be deleted");
    }
    private Runnable deleteAfterTime(Map<String, String> map, String shortLink){
        return new Runnable() {
            @Override
            public void run() {
                map.put("short_link",shortLink);
                try {
                    Thread.sleep(Long.valueOf(map.get("time")));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                CloseableHttpClient httpClient= HttpClients.createDefault();
                StringBuilder urlBase=new StringBuilder("http://localhost:8080/delete");
                urlBase.append("?short_link="+shortLink);
                urlBase.append("&id="+map.get("user_id"));
                HttpDelete httpDelete=new HttpDelete(urlBase.toString());

                try {
                    httpClient.execute(httpDelete);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    try {
                        httpClient.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }
}
