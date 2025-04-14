#!/bin/sh
# Input: recipe instructions (one per line)
# Output: Redis commands (can be piped to "redis-cli --pipe")
sed -e "s/'/\\\\\\\'/g" -e 's/"/\\\\\\"/g' | 
while read instructions; do
  printf 'RPUSH queue %s{"%s": "%s"}%s\n' "'" instructions "$instructions" "'"
done

