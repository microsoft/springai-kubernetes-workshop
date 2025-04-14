Bento is designed to work with JSON messages, which means that ideally:
- our input file should be in JSONL format
- our API endpoint should accept a JSON payload

But our input is a plain text file, with one recipe idea per line; and
our API endpoint needs classic url-encoded form data.

One way to handle our input file is with the "enqueue.yaml" file, which
is a Bento pipeline using a "csv" input where we disable the header row.
Each line is parsed as a row and ends up in a JSON array.
We set the delimiter as "|" since the default (",") could appear in our
input file. Then we use a processor to transform each line into a JSON
object with an "instructions" field.

Another way to handle the input is with the "generate-redis-commands.sh"
script, which reads the recipe instructions (one per line) and outputs
Redis commands that enqueue the instructions into Redis. The output of
that script is meant to be fed into "redis-cli --pipe". If Redis is
running in a Deployment or StatefulSet, it is therefore possible to do:

```bash
./generate-redis-commands.sh < 500-recipes.txt |
kubectl exec -i deploy/redis -- redis-cli --pipe
```

Finally, to consume the queue and feed it into our recipe generator API
endpoint, we can use another Bento pipeline - "dequeue.yaml" - which
will read the JSON objects from Redis, and use them to generate a POST
request body. It also needs to set the Content-Type header (otherwise
our API backend won't parse the request body correctly). We should also
probably escape the request body (with bloblang method `escape_url_query`),
but it looks like raw data works fine for now, so let's keep things simple.

The consumer can run in a Kubernetes cluster with the provided helmfile.

