package mega.waka.discord;

import mega.waka.entity.Member;
import mega.waka.repository.MemberRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DiscordListener extends ListenerAdapter {

    private final MemberRepository memberRepository;
    public DiscordListener(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
    private String formatAsHyperlink(String text, String url) {
        return "[" + text + "](" + url + ")";
    }
    public void createMessage(MessageReceivedEvent event, String message){
        User user = event.getAuthor();
        String returnMessage = "";
        String newMessage = "!!!!!***근무 시간 미달자 ***!!!! \n";
        EmbedBuilder embed = new EmbedBuilder();
        embed.addField("https://dev.app.megabrain.kr/waka", "", false);
        embed.setThumbnail("https://avatars.githubusercontent.com/inje-megabrain");
        embed.setFooter("메가브레인 와카 봇", "https://avatars.githubusercontent.com/inje-megabrain");
        switch(message){
            case "명령어" : returnMessage = "** 와카보드 봇 명령어 목록입니다.**\n waka! 전체순위 : 와카보드 전체 순위를 보여줍니다. \n waka! 개인순위 : 와카보드 개인 순위를 보여줍니다. \n waka! 명령어 : 와카보드 봇 명령어를 보여줍니다.";
            break;
            case "전체순위" :
                List<Member> memberList = memberRepository.findAll();
                Map<String, Integer> memberMap = new HashMap<>();
                for(Member member : memberList){
                    Pattern pattern = Pattern.compile("\\b(\\d+) hrs (\\d+) mins\\b");
                    Matcher matcher = pattern.matcher(member.getSevenDays());
                    if(matcher.find()){
                        int hour = Integer.parseInt(matcher.group(1));
                        int minute = Integer.parseInt(matcher.group(2));
                        memberMap.put(member.getName(),hour*60+minute);
                    }
                    else memberMap.put(member.getName(),0);
                }
                List<Map.Entry<String,Integer>> sortedList = new ArrayList<>(memberMap.entrySet());
                Collections.sort(sortedList, Map.Entry.comparingByValue(Comparator.reverseOrder()));
                embed.setTitle(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))+" 기준 " +"와카보드 전체 순위");
                embed.setColor(Color.green);
                int cnt=0;
                for(int i=0;i<sortedList.size();i++){
                    if(sortedList.get(i).getValue() <=10*60){
                        cnt++;
                        newMessage += (i+1)+" 등 - "+sortedList.get(i).getKey()+"\n -> "+sortedList.get(i).getValue()/60+"시간 "+sortedList.get(i).getValue()%60+"분\n";
                    }
                    else if(sortedList.get(i).getValue() > 10*60)
                        returnMessage += (i+1)+" 등 - "+sortedList.get(i).getKey()+"\n -> "+sortedList.get(i).getValue()/60+"시간 "+sortedList.get(i).getValue()%60+"분\n";
                    else newMessage += (i+1)+" 등 - "+sortedList.get(i).getKey()+"\n -> "+0+" 시간 "+0+"분\n";
                }
                if(cnt==0) newMessage += "현재 근무 시간 미달자가 없습니다.\n";
                embed.setDescription(returnMessage +"\n"+ newMessage);
                returnMessage = "현재 시간 기준 전체 순위입니다.";
            break;
            case "개인순위" :
                Member member;
                if(user.getName().equals("웅이")) member = memberRepository.findByName("신종웅");
                else if(user.getName().equals("jadru"))  member = memberRepository.findByName("박영건");
                else if(user.getName().equals("김동현hry")) member = memberRepository.findByName("김동현");
                else if(user.getName().equals("LeeByeongJin")) member = memberRepository.findByName("이병진");
                else if(user.getName().equals("ddeltas")) member = memberRepository.findByName("구본웅");
                else if(user.getName().equals("jyh")) member = memberRepository.findByName("정용휘");
                else if(user.getName().equals("gimuhyeon")) member = memberRepository.findByName("김우현");
                else if(user.getName().equals("병석")) member = memberRepository.findByName("성병석");
                else if(user.getName().equals("훈정이")) member = memberRepository.findByName("김훈정");
                else if(user.getName().equals("보경")) member = memberRepository.findByName("문보경");
                else if(user.getName().equals("Zn")) member = memberRepository.findByName("심아연");
                else if(user.getName().equals("Rod12")) member = memberRepository.findByName("이욱진");
                else if(user.getName().equals("재민")) member = memberRepository.findByName("김재민");
                else{
                    member = memberRepository.findByName(user.getName());
                }

                if(member ==null) returnMessage = "해당 멤버가 등록되지 않았습니다.";
                else{
                    List<Member> memberList2 = memberRepository.findAll();
                    Map<String, Integer> memberMap2 = new HashMap<>();
                    for(Member member2 : memberList2){
                        Pattern pattern = Pattern.compile("\\b(\\d+) hrs (\\d+) mins\\b");
                        Matcher matcher = pattern.matcher(member2.getSevenDays());
                        if(matcher.find()){
                            int hour = Integer.parseInt(matcher.group(1));
                            int minute = Integer.parseInt(matcher.group(2));
                            memberMap2.put(member2.getName(),(hour*60)+minute);
                        }
                        else memberMap2.put(member2.getName(),0);
                    }
                    int cnt2=0;
                    List<Map.Entry<String,Integer>> sortedList2 = new ArrayList<>(memberMap2.entrySet());
                    Collections.sort(sortedList2, Map.Entry.comparingByValue(Comparator.reverseOrder()));
                    embed.setTitle(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))+" 기준 " +"와카보드 개인 순위");
                    embed.setColor(Color.green);
                    for(int i=0;i<sortedList2.size();i++){
                        if(sortedList2.get(i).getKey().equals(member.getName())){
                            if(sortedList2.get(i).getValue() <=10*60) {
                                cnt2++;
                                newMessage += i+1+" 등 - "+member.getName()+"\n -> "+sortedList2.get(i).getValue()/60+"시간 "+sortedList2.get(i).getValue()%60+"분\n";
                            }
                            else if(sortedList2.get(i).getValue() > 10*60)
                                returnMessage += (i+1)+"등 - "+member.getName()+"\n -> "+sortedList2.get(i).getValue()/60+"시간 "+sortedList2.get(i).getValue()%60+"분\n";
                            else newMessage += (i+1)+" 등 - "+member.getName()+"\n -> "+0+" 시간 "+0+"분\n";
                        }
                    }
                    if(cnt2==0) newMessage += member.getName() + "님은 근무 시간 미달자가 아닙니다.\n";

                    embed.setDescription(returnMessage +"\n"+ newMessage);
                    returnMessage = "현재 시간 기준 순위입니다.";
                }
            break;
            default:
                embed.setTitle("명령어를 잘못 입력하셨습니다.");
                embed.setColor(Color.red);
                returnMessage = "등록된 waka!명령어를 입력해주세요.";
                embed.setDescription(returnMessage);
                returnMessage = "명령어를 다시 입력해주세요.";
            break;
        }
        sendMessage(event,returnMessage,embed);
    }
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        User user = event.getAuthor();
        Message message = event.getMessage();

        if(user.isBot()) return;
        String [] messageArray = message.getContentRaw().split(" ");
        if(messageArray[0].equals("waka!")){
             //1090659127417638943, 1116650046797135992
            if(!event.getChannel().getId().equals("1090659127417638943")) return;
            String [] messageArgs = Arrays.copyOfRange(messageArray,1,messageArray.length);
            for(String msg : messageArgs){
                createMessage(event,msg);

            }
        }
        super.onMessageReceived(event);
    }

    private void sendMessage(MessageReceivedEvent event, String message,EmbedBuilder embedBuilder){
        TextChannel textChannel = event.getChannel().asTextChannel();
        textChannel.sendMessage(message).setEmbeds(embedBuilder.build()).queue();
    }
}
