#!/bin/sh
set -eu

COOKBOOK_URL="https://raw.githubusercontent.com/micahcochran/json-cookbook/refs/heads/main/cookbook-100.json"
UPLOAD_URL="http://localhost:8080/upload"

curl -fsSL "$COOKBOOK_URL" | jq -c .[] | while read json; do
# Skip recipes with invalid JSON (the old-fashioned recipe seems broken )
echo "$json" | jq . >/dev/null || continue
echo "$json" | jq -r '"curl '$UPLOAD_URL' -d recipe=\"
Recipe title:
\(.name)

Ingredients:
\(.recipeIngredient | join("\n"))

Instructions:
\(.recipeInstructions | (if (.|type)=="array" then map(.text) | join ("\n") else . end))
\""' | sh
done

