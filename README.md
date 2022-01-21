# Service Chainer
Quickly and easily chain multiple service calls together with the ability to manipulate each input payload using templates.

# Currently Supports
- [Freemarker Template Engine](https://freemarker.apache.org/)
- [Velocity Template Engine](https://velocity.apache.org/)

# Special Template Input Constants
- **document** (Freemarker/Velocity). Represents incoming data payload json
- **JSONUtils** (Velocity). Helper class used to output Java Map to JSON.
- **Integer** (Velocity). java.lang.Integer static class
- **Double** (Velocity). java.lang.Double static class
- **Float** (Velocity). java.lang.Float static class
- **Boolean** (Velocity). java.lang.Boolean static class

# Screenshots
<img width="1220" alt="Screen Shot 2021-12-16 at 3 20 10 PM" src="https://user-images.githubusercontent.com/5247778/146443643-dbef994f-ffb9-4cfc-820b-c28042140a6d.png">
