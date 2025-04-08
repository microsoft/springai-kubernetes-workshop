package training.container.cookbook;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// To test generation with custom instructions, try for instance:
// curl localhost:8080/recipe -d "instructions=we want chocolate"

@RestController
public class RecipeGenerator {

    private final ChatClient chatClient;

    RecipeGenerator(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @PostMapping(value = "/recipe", produces = MediaType.APPLICATION_JSON_VALUE)
    public Recipe generateRecipe(@RequestParam(value = "instructions", defaultValue = "Make it vegan") String instructions) {
        System.out.println("We got these instructions:\n" + instructions + "\n");
        Recipe recipe = chatClient.prompt("Generate a cooking recipe for a delicious main dish. Take into account the following instructions:\n" + instructions).call().entity(Recipe.class);
        return recipe;
    }
}
