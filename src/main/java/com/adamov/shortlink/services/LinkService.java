package com.adamov.shortlink.services;

import com.adamov.shortlink.entity.Link;
import com.adamov.shortlink.entity.User;
import com.adamov.shortlink.repositories.LinkRepository;
import com.adamov.shortlink.repositories.UserRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

@Service
public class LinkService {
    private static final String CHARS="qwertyuiopasdfghjklzxcvbnm0123456789";
    private final Random random=new Random();

    private final LinkRepository linkRepository;
    private final UserRepository userRepository;

    public LinkService(LinkRepository linkRepository, UserRepository userRepository) {
        this.linkRepository = linkRepository;
        this.userRepository = userRepository;
    }

    public boolean createLink(Map<String, String> param) {
        Map<String, String> map=new TreeMap<>(param);
        if (linkRepository.findByFullLink(map.get("url"))!=null ||
                param.get("url")==null ||
                param.get("user_id")==null)
            return false;

        StringBuilder stringBuilder=new StringBuilder();
        for (Map.Entry<String, String> mapEntity:map.entrySet()){
            stringBuilder.append(mapEntity.getKey());
            stringBuilder.append("=");
            stringBuilder.append(mapEntity.getValue());
            stringBuilder.append("&");
        }


        String secretKey=userRepository.findById(Long.valueOf(map.get("user_id"))).orElse(null).getSecretKey();
        stringBuilder.append(secretKey);
        String hash= DigestUtils.sha1Hex(stringBuilder.toString());
        String sl=this.createShortEntity();
        if (sl!=null) {
            Link link = new Link();
            link.setShortLink(sl);
            link.setFullLink(map.get("url"));
            link.setHash(hash);
            User user=userRepository.findById(Long.valueOf(map.get("user_id"))).orElse(null);
            if (user!=null) user.addHash(hash);
            userRepository.save(user);
            linkRepository.save(link);
            return true;
        }

        return false;

    }

    private String createShortEntity(){

        int i1=random.nextInt(CHARS.length());
        int i2=random.nextInt(CHARS.length());
        int i3=random.nextInt(CHARS.length());
        String shortLink=String.valueOf(CHARS.charAt(i1))+CHARS.charAt(i2)+CHARS.charAt(i3);
        if (linkRepository.findByShortLink(shortLink)==null) return shortLink;
        else {
            this.createShortEntity();
            return null;
        }

    }




    public boolean delete(String shortLink, Long id) {
        Link link=linkRepository.findByShortLink(shortLink);
        User user=userRepository.findById(id).orElse(null);
        if (link==null||user==null){
            return false;}
        else{
            if (user.haveHash(link.getHash())){
            user.deleteHash(link.getHash());
            userRepository.save(user);
                linkRepository.delete(link);
        return true;}
        else return false;}

    }

    public Link findLink(String shortLink) {
        Link link=linkRepository.findByShortLink(shortLink);
        return link;
    }

    public String getShortLink(String fullLink){
        return linkRepository.findByFullLink(fullLink).getShortLink();
    }
}
