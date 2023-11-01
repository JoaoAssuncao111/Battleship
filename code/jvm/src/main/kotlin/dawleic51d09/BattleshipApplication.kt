package dawleic51d09


import dawleic51d09.http.pipeline.AuthenticationInterceptor
import dawleic51d09.http.pipeline.UserArgumentResolver
import dawleic51d09.repository.jdbi.configure
import dawleic51d09.utils.Sha256TokenEncoder
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import java.time.Instant


@SpringBootApplication
class BattleshipApplication {
	@Bean
	fun jdbi(): Jdbi {
		return Jdbi.create(
			PGSimpleDataSource().apply {
				setURL(System.getenv("JDBC_DATABASE_URL"))
			}
		).configure()
	}

	//@Bean
	//fun passwordEncoder() = BCryptPasswordEncoder()

	@Bean
	fun tokenEncoder() = Sha256TokenEncoder()

	@Bean
	fun clock() = object : Clock {
		override fun now() = Instant.now()
	}
}


@Configuration
class PipelineConfigurer(

	val authenticationInterceptor: AuthenticationInterceptor,
	val userArgumentResolver: UserArgumentResolver,
) : WebMvcConfigurer {

	override fun addInterceptors(registry: InterceptorRegistry) {
		registry.addInterceptor(authenticationInterceptor)
	}

	override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
		resolvers.add(userArgumentResolver)
	}
}


fun main(args: Array<String>) {
	runApplication<BattleshipApplication>(*args)
}