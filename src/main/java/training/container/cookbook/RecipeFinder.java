package training.container.cookbook;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

// To run the RAG part, you need:
// - a vector store (by default, we're using redis stack)
//   docker run -p 6379:6379 -d --name redis-stack redis/redis-stack
// - an embedding model (by default, it's mxbai-embed-large)
//   ollama pull mxbai-embed-large

@RestController
public class RecipeFinder {

    private final ChatClient chatClient;

    @Autowired VectorStore vectorStore;

    RecipeFinder(ChatClient.Builder builder) {
        chatClient = builder.build();
    }

    @PostMapping("/upload")
    public @ResponseBody String uploadRecipe(@RequestParam(value = "recipe") String recipe) {
        vectorStore.add(List.of(new Document(recipe)));
        return "OK\n";
    }

    @PostMapping(value = "/find", produces = MediaType.APPLICATION_JSON_VALUE)
    public Recipe findRecipe(@RequestParam(value = "instructions", defaultValue = "I love bananas") String instructions) {
        //String prompt = "Find a cooking recipe taking into account the following instructions:\n" + instructions + "\nGenerate a description of at least 100 words for that recipe as well. The description shouldn't repeat the list of instructions; instead, it should include some relatable details or personal anecdotes about the recipe.";
        String prompt = "Find a cooking recipe taking into account the following instructions: " + instructions + ". Generate a description of at least 100 words for that recipe as well. The description shouldn't repeat the list of instructions; instead, it should include some relatable details or personal anecdotes about the recipe.";
        QuestionAnswerAdvisor advisor = new QuestionAnswerAdvisor(vectorStore);
        Recipe recipe = chatClient.prompt(prompt).advisors(advisor).call().entity(Recipe.class);
        return recipe;
    }
}
