package mega.waka.discord;

import mega.waka.entity.Member;
import mega.waka.repository.MemberRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
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

    public void createMessage(MessageReceivedEvent event, String message){
        User user = event.getAuthor();
        String returnMessage = "";
        EmbedBuilder embed = new EmbedBuilder();
        embed.setFooter("와카보드 봇", "https://dev.megabrain.kr/waka");
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
                }
                List<Map.Entry<String,Integer>> sortedList = new ArrayList<>(memberMap.entrySet());
                Collections.sort(sortedList, Map.Entry.comparingByValue(Comparator.reverseOrder()));
                embed.setTitle(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))+" 기준 " +"와카보드 전체 순위");
                embed.setColor(Color.green);
                for(int i=0;i<sortedList.size();i++){
                    returnMessage += (i+1)+"위 "+sortedList.get(i).getKey()+" "+sortedList.get(i).getValue()/60+"시간 "+sortedList.get(i).getValue()%60+"분\n";
                }
                embed.setDescription(returnMessage);
            break;
            case "개인순위" :
                Member member = memberRepository.findByName(user.getName().substring(2));
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
                            memberMap2.put(member.getName(),hour*60+minute);
                        }
                    }
                    List<Map.Entry<String,Integer>> sortedList2 = new ArrayList<>(memberMap2.entrySet());
                    Collections.sort(sortedList2, Map.Entry.comparingByValue(Comparator.reverseOrder()));
                    embed.setTitle(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))+" 기준 " +"와카보드 개인 순위");
                    embed.setColor(Color.green);
                    for(int i=0;i<sortedList2.size();i++){
                        if(sortedList2.get(i).getKey().equals(user.getName().substring(2))){
                            returnMessage += (i+1)+"위 "+sortedList2.get(i).getKey()+" "+sortedList2.get(i).getValue()/60+"시간 "+sortedList2.get(i).getValue()%60+"분\n";
                        }
                    }
                    embed.setDescription(returnMessage);
                }
            break;
            default:
                embed.setTitle("명령어를 잘못 입력하셨습니다.");
                embed.setColor(Color.red);
                returnMessage = "등록된 waka!명령어를 입력해주세요.";
                embed.setDescription(returnMessage);
            break;
        }
        sendMessage(event,returnMessage,embed);
    }
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        User user = event.getAuthor();
        TextChannel textChannel = event.getChannel().asTextChannel();
        Message message = event.getMessage();

        if(user.isBot()) return;
        String [] messageArray = message.getContentRaw().split(" ");
        if(messageArray[0].equals("waka!")){
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
