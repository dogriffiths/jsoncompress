# Name
JsonCompress -- compression for tiny JSON strings.

# Usage

Create a `JsonCompressor` object, then pass strings to its `compressJson` method. It will return a compressed array of bytes.

    JsonCompressor compressor = new JsonCompressor();
    String s1 = "{\n"+
        "   \"type\":\"record\",\n"+
        "   \"location\":\"in the boardroom\",\n"+
        "   \"filename\":\"fred.aac\",\n"+
        "   \"sample_rate\":\"48000\",\n"+
        "   \"encoding_rate\":\"320000\",\n"+
        "   \"audio_type\":\"aac\",\n"+
        "   \"stereo\":\"false\"\n"+
        "}\n";
    byte[] compressedBytes = compressor.compressJson(s);

To expand the data, just call the `expandJson` method:

    String expanded = compressor.expandJson(compressedBytes);

# Description

For very small strings, normal compression algorithms start to fail. Strings shorter than 150-200 bytes might actually increase in size if run through GZip or Zip compression. 

`JsonCompress` was created to increase the amount of data that could be stored on NFC tags. NFC storage is so small that traditional compression methods can't be used effectively.

Many NFC tags come with just 144 bytes of storage. Typically, some of this space will need to be reserved for overhead storage:

- The tag id (7 bytes)
- The AAR record -- if you want the tag to launch a custom Android app (about 40 bytes)
- Other record headers (around 6 bytes)

This means that you typically have under 100 bytes of storage available. This means that many developers have to switch to a custom, binary format. Unfortunately, this fixes the data schema in the code, which reduces flexibility.

By using compressed JSON, you can get similar storage capacities to custom binary formats, plus the schema is defined in the data itself. This allows you to create a heterogeneous set of tags which have data relevant to a context.

Plus, JSON is typically far easier to deal with in code, than other formats.

# Compression benchmarks

The `Smaz` library is commonly used to compress small strings. Here is a comparison of the compression ratios of `Smaz` and `JsonCompress` using the string-benchmarks from the Smaz repo, wrapped in simple JSON strings.

Larger values are better. Negative values mean that the compressor actually increased the size.

| String                                                                                      | Smaz | JsonCompress |
| ------------------------------------------------------------------------------------------- | ---- | ------------ |
| '{"t":"This is a small string"}'                                                            |  28% |          47% |
| '{"t":"foobar"}'                                                                            |   0% |          50% |
| '{"t":"the end"}'                                                                           |  12% |          54% |
| '{"t":"not-a-g00d-Exampl333"}'                                                              | -16% |          36% |
| '{"t":"Smaz is a simple compression library"}'                                              |  26% |          44% |
| '{"t":"Nothing is more difficult, and therefore more precious, than to be able to decide"}' |  41% |          41% |
| '{"t":"this is an example of what works very well with smaz"}'                              |  37% |          44% |
| '{"t":"1000 numbers 2000 will 10 20 30 compress very little"}'                              |   5% |          42% |
| '{"t":"Nel mezzo del cammin di nostra vita, mi ritrovai in una selva oscura"}'              |  26% |          28% |
| '{"t":"Mi illumino di immenso"}'                                                            |  19% |          34% |
| '{"t":"L'autore di questa libreria vive in Sicilia"}'                                       |  19% |          28% |
| '{"t":"http://google.com"}'                                                                 |  28% |          48% |
| '{"t":"http://programming.reddit.com"}'                                                     |  33% |          49% |
| '{"t":"http://github.com/antirez/smaz/tree/master"}'                                        |  32% |          38% |

# Prototype compression

If you have a good idea of what the structure of your data will be, you can increase the amount of compression by providing a prototype example. This is a piece of JSON that has the same kinds of field names the data will include. This allows `JsonCompress` to store the packed-binary format more efficiently.

Example:

    String prototype = "{\n"+
        "   \"type\":\"record\",\n"+
        "   \"location\":\"somewhere\",\n"+
        "   \"filename\":\"bill.mp3\",\n"+
        "   \"sample_rate\":\"8000\",\n"+
        "   \"encoding_rate\":\"3000\",\n"+
        "   \"audio_type\":\"mp3\",\n"+
        "   \"use_video\":\"true\",\n"+
        "   \"schedule\":[\"09:00\",\"12:00\"],\n"+
        "   \"stereo\":\"true\"\n"+
        "}\n";
    String s = "{\n"+
        "   \"type\":\"record\",\n"+
        "   \"location\":\"in the boardroom\",\n"+
        "   \"filename\":\"fred.aac\",\n"+
        "   \"sample_rate\":\"48000\",\n"+
        "   \"encoding_rate\":\"320000\",\n"+
        "   \"audio_type\":\"aac\",\n"+
        "   \"use_video\":\"false\",\n"+
        "   \"schedule\":[\"11:00\",\"14:00\",\"18:00\"],\n"+
        "   \"stereo\":\"false\"\n"+
        "}\n";
    JsonCompressor compressorWithPrototype = new JsonCompressor(prototype);
    byte[] compressWithPrototype = compressorWithPrototype.compressJson(s);

You can then expand the compressed bytes by calling `expandJson` again.

In this example, non-prototype compression would shrink the original 209 byte JSON string to 108 bytes. *With* prototype compression it is shrunk to 75 bytes.

*Note:* If you compress data using a prototype, then the `JsonCompressor` that expands it must be using the *same* prototype.

Under some circumstances, the prototype will not improve compression. If this happens, `JsonCompress` will silently fall back to ordinary, non-prototype compression.

# Further reading

There is a [blog](http://www.blackpepper.co.uk/theres-plenty-of-room-at-the-bottom-nfc/ "Blog") which provides further information on how an early version of `JsonCompress` was created.
