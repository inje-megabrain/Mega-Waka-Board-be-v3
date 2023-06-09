package mega.waka;

import mega.waka.discord.DiscordListener;
import mega.waka.repository.MemberRepository;
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

	public ServerApplication(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(ServerApplication.class, args);
		DiscordToken discordBotTokenEntity = context.getBean(DiscordToken.class);
		String discordBotToken = discordBotTokenEntity.getToken();

		JDA jda = JDABuilder.createDefault(discordBotToken)
				.setActivity(Activity.playing("코딩"))
				.enableIntents(GatewayIntent.MESSAGE_CONTENT)
				.addEventListeners(new DiscordListener(memberRepository))
				.build();
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
