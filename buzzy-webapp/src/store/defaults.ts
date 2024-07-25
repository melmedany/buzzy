import {ISettings} from "@src/types";
import {delay} from "@src/utils";

export const defaultSettings: ISettings = {
    lastSeen: false,
    readReceipt: false,
    joiningGroups: false,
    privateMessages: false,
    darkMode: false,
    borderedTheme: false,
    allowNotifications: false,
    keepNotifications: false,
    preferredLanguage: navigator.language || 'en',
}

export const conversations = [];

export const archive = [];

export const notifications = [];

export const calls = [];

export const activeCall = {
  id: "6",
  type: "voice",
  status: "dialing",
  direction: "outgoing",
  date: new Date(),
  length: "01:12",
  members: [
    {
      id: "1",
      firstname: "Dawn",
      lastname: "Sabrina",
      lastSeen: new Date(),
      username: "",
      email: "sabrina@gmail.com",
      avatar:
        "https://images.unsplash.com/photo-1494790108377-be9c29b29330?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=387&q=80",
    },
    {
      id: "8",
      email: "user@gmail.com",
      username: "",
      firstname: "Evelyn",
      lastname: "Billy",
      lastSeen: new Date(),
      avatar:
        "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Nnx8cGVvcGxlfGVufDB8fDB8fA%3D%3D&auto=format&fit=crop&w=500&q=60",
    },
  ],
  adminIds: ["1"],
}

export const updateAccount = async () => {
  await delay(4000);

  return await {
    data: {
      detail: "Your account has been updated successfully",
    },
  }
}

export const attachments = [
  {
    id: "6",
    type: "image",
    name: "forest.jpg",
    size: "21 MB",
    url: "https://images.unsplash.com/photo-1664021975758-78d83898225d?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxlZGl0b3JpYWwtZmVlZHwxOXx8fGVufDB8fHx8&auto=format&fit=crop&w=500&q=60",
  },
  {
    id: "7",
    type: "image",
    name: "pumkins.jpg",
    size: "22 MB",
    url: "https://images.unsplash.com/photo-1664031315855-955dbca83172?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=870&q=80",
  },
  {
    id: "8",
    type: "image",
    name: "mountain.jpg",
    size: "23 MB",
    url: "https://images.unsplash.com/photo-1664091729644-07a158d7c4ca?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxlZGl0b3JpYWwtZmVlZHwyNHx8fGVufDB8fHx8&auto=format&fit=crop&w=500&q=60",
  },
  {
    id: "9",
    type: "file",
    name: "lecture-10.pdf",
    size: "52.4 MB",
    url: "https://images.unsplash.com/photo-1664091729644-07a158d7c4ca?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxlZGl0b3JpYWwtZmVlZHwyNHx8fGVufDB8fHx8&auto=format&fit=crop&w=500&q=60",
  },
  {
    id: "10",
    type: "video",
    name: "fun-video.mp4",
    size: "11.4 MB",
    url: "https://images.unsplash.com/photo-1559705421-4ae9bf6fabb5?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=870&q=80",
  },
];

export default {
  defaultSettings,
  archive,
  conversations,
  notifications,
  calls,
  activeCall,
} as const;
