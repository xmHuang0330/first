package test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;


@Slf4j
@SpringBootApplication
@ComponentScan(basePackages = {"fayi"})
public class APP implements CommandLineRunner {

    public static void main(String[] args) {
//        String uuid = Utils.RunCommand(new String[]{"bash","-c"},new String[]{"dmidecode","-s","system-uuid"});
//        if(!"4n4n4544-0050-4610-8048-n8n04f373433".replaceAll("n","c").equals(uuid)){
//            log.error("System not supported.");
//        }else {
            SpringApplication.run(APP.class, args);
//        }
    }

    @Override
    public void run(String... args) {
        System.out.println("started...");
    }
}
