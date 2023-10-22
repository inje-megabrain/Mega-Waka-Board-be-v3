package mega.waka;

import mega.waka.discord.DiscordListener;
import mega.waka.repository.MemberRepository;
import mega.waka.service.MemberService;
import mega.waka.service.SevenDaysWakaService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class ServerApplication {
	private static MemberRepository memberRepository;
	private static SevenDaysWakaService sevenDaysWakaService;

	public ServerApplication(MemberRepository memberRepository, SevenDaysWakaService sevenDaysWakaService) {
		this.memberRepository = memberRepository;
		this.sevenDaysWakaService = sevenDaysWakaService;
	}

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(ServerApplication.class, args);
		DiscordToken discordBotTokenEntity = context.getBean(DiscordToken.class);
		String discordBotToken = discordBotTokenEntity.getToken();

		JDA jda = JDABuilder.createDefault(discordBotToken)
				.setActivity(Activity.playing("코딩"))
				.enableIntents(GatewayIntent.MESSAGE_CONTENT)
				.addEventListeners(new DiscordListener(memberRepository, sevenDaysWakaService))
				.build();
		jda.upsertCommand("개인순위","금주 와카타임 개인순위를 제공합니다.").setGuildOnly(true).queue();
		jda.upsertCommand("전체순위","금주 와카타임 전체순위를 제공합니다.").setGuildOnly(true).queue();
	}
	@Component
	class DiscordToken {
		@Value("${discord.bot.token}")
		private String token;
		public String getToken() {
			return token;
		}
	}

}
