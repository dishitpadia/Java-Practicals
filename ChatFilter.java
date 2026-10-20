public class ChatFilter {
    
    public int searchMessages(String[] messages, String keyword, StringBuilder report)
    {
        int counter = 0;

        for(int i = 0; i < messages.length; ++i)
        {
            String words[] = messages[i].split(" ", 3);

            if(words.length < 3)
            {
                continue;
            }

            String time = words[0];
            String name = words[1];
            String message = words[2];

            if(message.toLowerCase().contains(keyword.toLowerCase()))
            {
                counter++;
                report.append(time + " " + name + " " + message + " " + "\n");
            }
        }

        return counter;
    }
}
