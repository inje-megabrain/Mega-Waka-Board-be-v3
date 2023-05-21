package mega.waka.service;

import mega.waka.entity.*;
import mega.waka.entity.editor.FourteenDaysEditor;
import mega.waka.entity.language.FourteenDaysLanguage;
import mega.waka.entity.project.FourteenDaysProject;
import mega.waka.repository.*;
import mega.waka.repository.editor.FourteenDaysEditorRepository;
import mega.waka.repository.language.FourteenDaysLanguageRepository;
import mega.waka.repository.project.FourteenDaysProjectRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Service
public class FourteenDaysWakaService {

    private final MemberRepository memberRepository;
    private final FourteenDaysEditorRepository fourtyDaysEditorRepository;
    private final FourteenDaysLanguageRepository fourtyDaysLanguageRepository;
    private final FourteenDaysProjectRepository fourtyDaysProjectRepository;
    private Map<String,String> languageList = new HashMap<>();
    private Map<String,String> projectList = new HashMap<>();
    private Map<String,String> editList = new HashMap<>();

    public FourteenDaysWakaService(MemberRepository memberRepository, FourteenDaysEditorRepository fourtyDaysEditorRepository, FourteenDaysLanguageRepository fourtyDaysLanguageRepository, FourteenDaysProjectRepository fourtyDaysProjectRepository) {
        this.memberRepository = memberRepository;
        this.fourtyDaysEditorRepository = fourtyDaysEditorRepository;
        this.fourtyDaysLanguageRepository = fourtyDaysLanguageRepository;
        this.fourtyDaysProjectRepository = fourtyDaysProjectRepository;
    }
    public void update_FourtyDays() {  // member의 코딩 시간 조회 api
        List<Member> members = memberRepository.findAll();
        String responseData="";
        try {
            RestTemplate restTemplate = new RestTemplate();
            String apiUrl ="https://wakatime.com/api/v1/users/current/summaries";
            for(Member member : members){
                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl)
                        .queryParam("range","last_"+14+"_days");

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
                JSONArray data = (JSONArray) jsonObject.get("data");
                

                
                JSONObject total = (JSONObject) jsonObject.get("cumulative_total");
                System.out.println(total);
                member.setFourteenDays(total.get("text").toString());
                for(int i=0;i<data.size();i++){
                    JSONObject obj = (JSONObject) data.get(i);
                    JSONArray languages = (JSONArray) obj.get("languages");
                    JSONArray editors = (JSONArray) obj.get("editors");
                    JSONArray projects = (JSONArray) obj.get("projects");

                    if(languages.isEmpty() || editors.isEmpty() || projects.isEmpty()) continue;
                    else {
                        set_Language(languages);
                        set_Project(projects);
                        set_Editor(editors);
                    }
                }
                set_Member_By_Language(member);
                set_Member_By_Editor(member);
                set_Member_By_Project(member);
                editList.clear();
                languageList.clear();
                projectList.clear();
            }

        } catch (Exception e) {
            e.printStackTrace();
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
    //------------------------------------set----------------------------------------/
    private void set_Member_By_Language(Member member) {
        if(member.getFourtyDaysLanguages().size()==0){
            for (String key : languageList.keySet()) {

                FourteenDaysLanguage language = new FourteenDaysLanguage().builder()
                        .name(key)
                        .time(languageList.get(key))
                        .build();
                fourtyDaysLanguageRepository.save(language);
                member.getFourtyDaysLanguages().add(language);
                memberRepository.save(member);
            }
        }
        else{
            for(int i=0; i<member.getFourtyDaysLanguages().size();i++){
                boolean flag = false;
                String name="";
                for(String key : languageList.keySet()){
                    name =key;
                    if(member.getFourtyDaysLanguages().get(i).getName().equals(key)){
                        member.getFourtyDaysLanguages().get(i).setTime(languageList.get(key));
                        flag = true;
                    }
                    if(member.getFourtyDaysLanguages().get(i).getTime().equals("0:0")){
                        member.getFourtyDaysLanguages().remove(i);
                    }
                }
                if(flag==false){
                    FourteenDaysLanguage language = new FourteenDaysLanguage().builder()
                            .name(name)
                            .time(languageList.get(name))
                            .build();
                    fourtyDaysLanguageRepository.save(language);
                    member.getFourtyDaysLanguages().add(language);
                    memberRepository.save(member);
                }
            }
        }

    }
    private void set_Member_By_Project(Member member) {
        if(member.getFourtyDaysProjects().size()==0){
            for (String key : projectList.keySet()) {

                FourteenDaysProject project = new FourteenDaysProject().builder()
                        .name(key)
                        .time(projectList.get(key))
                        .build();
                fourtyDaysProjectRepository.save(project);
                member.getFourtyDaysProjects().add(project);
                memberRepository.save(member);
            }
        }
        else{
            for(int i=0; i<member.getFourtyDaysProjects().size();i++){
                boolean flag = false;
                String name="";
                for(String key : projectList.keySet()){
                    name = key;
                    if(member.getFourtyDaysProjects().get(i).getName().equals(key)){
                        member.getFourtyDaysProjects().get(i).setTime(projectList.get(key));
                        flag=true;
                    }
                    if(member.getFourtyDaysProjects().get(i).getTime().equals("0:0")){
                        member.getFourtyDaysProjects().remove(i);
                    }
                }
                if(flag==false){
                    FourteenDaysProject project = new FourteenDaysProject().builder()
                            .name(name)
                            .time(projectList.get(name))
                            .build();
                    fourtyDaysProjectRepository.save(project);
                    member.getFourtyDaysProjects().add(project);
                    memberRepository.save(member);
                }
            }
        }
    }

    private void set_Member_By_Editor(Member member) {
        if (member.getFourtyDaysEditors().size() == 0) {
            for (String key : editList.keySet()) {

                FourteenDaysEditor editor = new FourteenDaysEditor().builder()
                        .name(key)
                        .time(editList.get(key))
                        .build();
                fourtyDaysEditorRepository.save(editor);
                member.getFourtyDaysEditors().add(editor);
                memberRepository.save(member);
            }
        } else {
            for (int i = 0; i < member.getFourtyDaysEditors().size(); i++) {
                boolean flag = false;
                String name = "";
                for (String key : editList.keySet()) {
                    name = key;
                    if (member.getFourtyDaysEditors().get(i).getName().equals(key)) {
                        member.getFourtyDaysEditors().get(i).setTime(editList.get(key));
                    }
                    if(member.getFourtyDaysEditors().get(i).getTime().equals("0:0")){
                        member.getFourtyDaysEditors().remove(i);
                    }
                }
                if (flag == false) {
                    FourteenDaysEditor editor = new FourteenDaysEditor().builder()
                            .name(name)
                            .time(editList.get(name))
                            .build();
                    fourtyDaysEditorRepository.save(editor);
                    member.getFourtyDaysEditors().add(editor);
                    memberRepository.save(member);
                }
            }
        }
    }
}
