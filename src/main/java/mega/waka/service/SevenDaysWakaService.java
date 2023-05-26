package mega.waka.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import mega.waka.entity.Member;
import mega.waka.entity.editor.SevenDaysEditor;
import mega.waka.entity.language.SevenDaysLanguage;
import mega.waka.entity.project.SevenDaysProject;
import mega.waka.repository.MemberRepository;
import mega.waka.repository.editor.SevenDaysEditorRepository;
import mega.waka.repository.language.SevenDaysLanguageRepository;
import mega.waka.repository.project.SevendaysProjectRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class SevenDaysWakaService {

    private final SevenDaysEditorRepository sevenDaysEditorRepository;

    private final SevenDaysLanguageRepository sevenDaysLanguageRepository;

    private final SevendaysProjectRepository sevendaysProjectRepository;
    private final MemberRepository memberRepository;

    private Map<String,String> languageList = new HashMap<>();
    private Map<String,String> projectList = new HashMap<>();
    private Map<String,String> editList = new HashMap<>();

    public SevenDaysWakaService(SevenDaysEditorRepository sevenDaysEditorRepository, SevenDaysLanguageRepository sevenDaysLanguageRepository, SevendaysProjectRepository sevendaysProjectRepository, MemberRepository memberRepository) {
        this.sevenDaysEditorRepository = sevenDaysEditorRepository;
        this.sevenDaysLanguageRepository = sevenDaysLanguageRepository;
        this.sevendaysProjectRepository = sevendaysProjectRepository;
        this.memberRepository = memberRepository;
    }
    @JsonIgnore
    public void update_SevenDays(){
        List<Member> members = memberRepository.findAll();
        String responseData="";
        try {
            RestTemplate restTemplate = new RestTemplate();
            String apiUrl ="https://wakatime.com/api/v1/users/current/summaries?range=last_7_days";
            for (Member member : members) {

                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl);

                HttpHeaders headers = new HttpHeaders();
                headers.setBasicAuth(member.getSecretKey(),"");

                ResponseEntity<String> response = restTemplate.exchange(
                        builder.toUriString(),
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        String.class
                );
                responseData = response.getBody();

                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(responseData);
                System.out.println("jsonObject = " + jsonObject);
                JSONArray data = (JSONArray) jsonObject.get("data");
                for(int i=0;i<data.size();i++){
                    JSONObject obj = (JSONObject) data.get(i);
                    JSONObject cumulative_total = (JSONObject) obj.get("cumulative_total");
                    member.setSevenDays((String) cumulative_total.get("text"));
                    System.out.println("cumulative_total = " + cumulative_total);
                    memberRepository.save(member);
                }

                /*JSONArray languages = (JSONArray) data.get("languages");
                JSONArray editors = (JSONArray) data.get("editors");
                JSONArray projects = (JSONArray) data.get("projects");

                set_Member_By_Language(member,languages);
                set_Member_By_Editor(member,editors);
                set_Member_By_Project(member,projects);*/

                DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
                if(dayOfWeek.getDisplayName(TextStyle.FULL, Locale.KOREAN).equals("금요일") && !member.getMoney().getUpdateDate().isEqual(LocalDate.now())){
                    int money = member.getMoney().getAmount();
                    String [] time = member.getSevenDays().split(" ");
                    int hour = Integer.valueOf(time[0]);
                    money += hour *9620;
                    System.out.println(money);
                    member.getMoney().setAmount(money);
                    member.getMoney().setUpdateDate(LocalDate.now());
                    memberRepository.save(member);
                }

            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    private Map<String,String> set_Language(JSONArray languages){
        for(int j=0;j<languages.size();j++) {
            JSONObject index = (JSONObject) languages.get(j);
            String name = (String) index.get("name");
            Long hour = (Long) index.get("hours");
            Long min = (Long) index.get("minutes");
            if(min>=60){
                hour+=min/60;
                min = min%60;
            }
            if(languageList.containsKey(name)){
                String[] time = languageList.get(name).split(":");
                Long hour2 = Long.parseLong(time[0]);
                Long min2 = Long.parseLong(time[1]);
                hour += hour2;
                min += min2;
                if(min>=60){
                    hour+=min/60;
                    min = min%60;
                }
                languageList.replace(name,hour+":"+min);
            }
            else{

                languageList.put(name,hour+":"+min);
            }
        }

        return languageList;
    }
    private Map<String,String> set_Project(JSONArray projects){

        for(int j=0;j<projects.size();j++) {
            JSONObject index = (JSONObject) projects.get(j);
            String name = (String) index.get("name");
            Long hour = (Long) index.get("hours");
            Long min = (Long) index.get("minutes");
            if(min>=60){
                hour+=min/60;
                min = min%60;
            }
            if(projectList.containsKey(name)){
                String[] time = projectList.get(name).split(":");
                Long hour2 = Long.parseLong(time[0]);
                Long min2 = Long.parseLong(time[1]);
                hour += hour2;
                min += min2;
                if(min>=60){
                    hour+=min/60;
                    min = min%60;
                }
                projectList.replace(name,hour+":"+min);
            }
            else{

                projectList.put(name,hour+":"+min);
            }
        }
        return projectList;

    }
    private Map<String,String> set_Editor(JSONArray editor){

        for(int j=0;j<editor.size();j++) {
            JSONObject index = (JSONObject) editor.get(j);
            String name = (String) index.get("name");
            Long hour = (Long) index.get("hours");
            Long min = (Long) index.get("minutes");
            if(min>=60){
                hour+=min/60;
                min = min%60;
            }
            if(editList.containsKey(name)){
                String[] time = editList.get(name).split(":");
                Long hour2 = Long.parseLong(time[0]);
                Long min2 = Long.parseLong(time[1]);
                hour += hour2;
                min += min2;
                if(min>=60){
                    hour+=min/60;
                    min = min%60;
                }
                editList.replace(name,hour+":"+min);
            }
            else{

                editList.put(name,hour+":"+min);
            }
        }
        return editList;

    }
    private void set_Member_By_Language(Member member, JSONArray languages) {
        if (member.getSevenlanguages().size() == 0) {
            for (int i=0;i<languages.size();i++) {
                JSONObject obj = (JSONObject) languages.get(i);
                JSONObject name = (JSONObject) obj.get("name");
                JSONObject hour = (JSONObject) obj.get("hours");
                JSONObject min = (JSONObject) obj.get("minutes");
                String time = hour.get("text").toString() + ":" + min.get("text").toString();
                SevenDaysLanguage language = new SevenDaysLanguage().builder()
                        .name(name.toString())
                        .time(time)
                        .build();
                sevenDaysLanguageRepository.save(language);
                member.getSevenlanguages().add(language);
                memberRepository.save(member);
            }
        } else {
            for (int i = 0; i < member.getSevenlanguages().size(); i++) {
                boolean flag = false;
                String name = "";
                String time = "";
                for (int j=0;j<languages.size();j++) {
                    JSONObject obj = (JSONObject) languages.get(i);
                    JSONObject names = (JSONObject) obj.get("name");
                    JSONObject hour = (JSONObject) obj.get("hours");
                    JSONObject min = (JSONObject) obj.get("minutes");
                    time = hour.get("text").toString() + ":" + min.get("text").toString();
                    name = names.toString();
                    if (member.getSevenlanguages().get(i).getName().equals(names.toString())) {
                        member.getSevenlanguages().get(i).setTime(time);
                        flag = true;
                    }
                    if(member.getSevenlanguages().get(i).getTime().equals("0:0")){
                        member.getSevenlanguages().remove(i);
                    }
                }
                if (flag == false) {
                    SevenDaysLanguage language = new SevenDaysLanguage().builder()
                            .name(name)
                            .time(time)
                            .build();
                    sevenDaysLanguageRepository.save(language);
                    member.getSevenlanguages().add(language);
                    memberRepository.save(member);
                }
            }
        }
    }
    private void set_Member_By_Project(Member member, JSONArray projects) {
        if (member.getSevenprojects().size() == 0) {
            for (int i=0;i<projects.size();i++) {
                JSONObject obj = (JSONObject) projects.get(i);
                JSONObject name = (JSONObject) obj.get("name");
                JSONObject hour = (JSONObject) obj.get("hours");
                JSONObject min = (JSONObject) obj.get("minutes");
                String time = hour.get("text").toString() + ":" + min.get("text").toString();
                SevenDaysProject project = new SevenDaysProject().builder()
                        .name(name.toString())
                        .time(time)
                        .build();
                sevendaysProjectRepository.save(project);
                member.getSevenprojects().add(project);
                memberRepository.save(member);
            }
        } else {
            for (int i = 0; i < member.getSevenprojects().size(); i++) {
                boolean flag = false;
                String name = "";
                String time = "";
                for (int j=0;j<projects.size();j++) {
                    JSONObject obj = (JSONObject) projects.get(i);
                    JSONObject names = (JSONObject) obj.get("name");
                    JSONObject hour = (JSONObject) obj.get("hours");
                    JSONObject min = (JSONObject) obj.get("minutes");
                    time = hour.get("text").toString() + ":" + min.get("text").toString();
                    name = names.toString();
                    if (member.getSevenprojects().get(i).getName().equals(names.toString())) {
                        member.getSevenprojects().get(i).setTime(time);
                        flag = true;
                    }
                    if(member.getSevenprojects().get(i).getTime().equals("0:0")){
                        member.getSevenprojects().remove(i);
                    }
                }
                if (flag == false) {
                    SevenDaysProject project = new SevenDaysProject().builder()
                            .name(name)
                            .time(time)
                            .build();
                    sevendaysProjectRepository.save(project);
                    member.getSevenprojects().add(project);
                    memberRepository.save(member);
                }
            }
        }
    }
    private void set_Member_By_Editor(Member member,JSONArray editors) {
        if (member.getSeveneditors().size() == 0) {
            for (int i = 0; i < editors.size(); i++) {
                JSONObject obj = (JSONObject) editors.get(i);
                JSONObject name = (JSONObject) obj.get("name");
                JSONObject hour = (JSONObject) obj.get("hours");
                JSONObject min = (JSONObject) obj.get("minutes");
                String time = hour.get("text").toString() + ":" + min.get("text").toString();
                SevenDaysEditor editor = new SevenDaysEditor().builder()
                        .name(name.toString())
                        .time(time)
                        .build();
                sevenDaysEditorRepository.save(editor);
                member.getSeveneditors().add(editor);
                memberRepository.save(member);
            }
        } else {
            for (int i = 0; i < member.getSeveneditors().size(); i++) {
                boolean flag = false;
                String name = "";
                String time = "";
                for (int j = 0; j < editors.size(); j++) {
                    JSONObject obj = (JSONObject) editors.get(i);
                    JSONObject names = (JSONObject) obj.get("name");
                    JSONObject hour = (JSONObject) obj.get("hours");
                    JSONObject min = (JSONObject) obj.get("minutes");
                    time = hour.get("text").toString() + ":" + min.get("text").toString();
                    name = names.toString();
                    if (member.getSeveneditors().get(i).getName().equals(names.toString())) {
                        member.getSeveneditors().get(i).setTime(time);
                        flag = true;
                    }
                    if (member.getSeveneditors().get(i).getTime().equals("0:0")) {
                        member.getSeveneditors().remove(i);
                    }
                }
                if (flag == false) {
                    SevenDaysEditor editor = new SevenDaysEditor().builder()
                            .name(name)
                            .time(time)
                            .build();
                    sevenDaysEditorRepository.save(editor);
                    member.getSeveneditors().add(editor);
                    memberRepository.save(member);
                }
            }
        }
    }
}
