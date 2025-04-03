package training.container.cookbook;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class WebUI {

    private final RecipeGenerator recipeGenerator;

    @Value("${spring.ai.ollama.chat.options.model}")
    private String model;

    WebUI(RecipeGenerator recipeGenerator) {
        this.recipeGenerator = recipeGenerator;
    }

    @GetMapping("/model")
    public @ResponseBody String getModel() {
        return model;
    }

    @GetMapping("/")
    public String getIndex(Model model) {
        model.addAttribute("aiModel", getModel());
        return "index";
    }

    @PostMapping("/")
    public String generateAndShowRecipe(Model model, @RequestParam(value = "instructions") String instructions) {
        Recipe recipe = recipeGenerator.generateRecipe(instructions);
        model.addAttribute("recipe", recipe);
        return getIndex(model);
    }

}
