import useStore from "@src/store/store";
import {ApiErrorCode, ICall, IContact, IConversation, IMessage, IRecording,} from "@src/types";
import {useRoute} from "vue-router";
import i18n from "@src/i18n";
import moment from 'moment'
import 'moment/dist/locale/nl'
import 'moment-timezone'

/**
 * combine first name and last name of a contact.
 * @param contact
 * @returns A string the combines the first and last names.
 */
export const getFullName = (contact: IContact, hyphen?: boolean) => {
  if (hyphen) {
    return contact.firstname + "-" + contact.lastname;
  } else {
    return contact.firstname + " " + contact.lastname;
  }
}

/**
 * get the other contact that is not the authenticated user.
 * @param conversation
 * @returns A contact object representing the other user in the conversation.
 */

export const getOddContact = (conversation: IConversation) => {
  const store = useStore();

  if (conversation.contacts.length == 1) {
    return conversation.contacts[0];
  }

  let oddContact;

  for (let contact of conversation.contacts) {
    if (store.user && contact.id !== store.user.id) {
      oddContact = contact;
    }
  }

  return oddContact;
}

/**
 * get avatar based on conversation type.
 * @param conversation
 * @returns A string representing the url to the avatar image
 */
export const getAvatar = (conversation: IConversation) => {
  if (["group", "broadcast"].includes(conversation.type)) {
    return conversation?.avatar;
  } else {
    let oddContact = getOddContact(conversation);
    return oddContact?.avatar;
  }
}

/**
 * get name based on conversation type.
 * @param conversation
 * @returns String
 */
export const getName = (conversation: IConversation, hyphen?: boolean) => {
  if (["group", "broadcast"].includes(conversation?.type)) {
    if (hyphen) {
      return (conversation.name as string).split(" ").join("-");
    } else {
      return conversation.name;
    }
  } else {
    let oddContact = getOddContact(conversation);
    if (oddContact) {
      return getFullName(oddContact, hyphen);
    }
  }
}

/**
 * trim a string when it reaches a certain length and adds three dots
 * at the end.
 * @param text
 * @param maxLength
 * @returns A string that is trimmed according the length provided
 */
export const shorten = (message: IMessage | string, maxLength: number = 23) => {
  let text: string | IRecording | undefined;

  if (typeof message === "string") {
    text = message;
  } else {
    text = message?.content;
  }

  if (text && typeof text === "string") {
    let trimmedString = text;
    if (text.length > maxLength) {
      // trim the string to the maximum length.
      trimmedString = trimmedString.slice(0, maxLength);
      // add three dots to indicate that there is more to the message.
      trimmedString += "...";
    }
    return trimmedString;
  }

  return "";
}

/**
 * test if the message contains attachments
 * @param message
 * @returns A boolean indicating whether the message has attachments
 */
export const hasAttachments = (message: IMessage) => {
  let attachments = message?.attachments;
  return attachments && attachments.length > 0;
}

/**
 * extract the id of the active conversaiton from the url
 */
export const getActiveConversationId = () => {
  const route = useRoute();
  return route.params.id ? route.params.id : undefined;
}

/**
 * get index of the conversation inside the conversations array
 * @param conversationId
 * @returns A number indicating the index of the conversation.
 */
export const getConversationIndex = (
  conversationId: string
): number | undefined => {
  let conversationIndex;
  const store = useStore();

  store.conversations.forEach((conversation, index) => {
    if (conversation.id === conversationId) {
      conversationIndex = index;
    }
  });

  return conversationIndex;
}

export const getMessageIndex = (
    messageId: string,
    conversationId: string
): number | undefined => {
  let conversationIndex = getConversationIndex(conversationId);
  let messageIndex;
  const store = useStore();

  store.conversations[conversationIndex!].messages.forEach((message, index) => {
    if (message.id === messageId) {
      messageIndex = index;
    }
  });

  return messageIndex;
}

/**
 * takes a call object and returns all the members
 * of the call except the authenticated user.
 * @param call
 * @returns An array containing the contacts participating in the call
 */
export const getOtherMembers = (call: ICall) => {
  const store = useStore();
  let members = [];

  if (call) {
    for (let member of call.members) {
      if (store.user && member.id !== store.user.id) {
        members.push(member);
      }
    }
  }

  return members;
}

/**
 * takes a call object and returns a name for the call
 * @param call
 * @param full
 * @param maxLength
 * @returns A string representing name of the call.
 */
export const getCallName = (
  call: ICall,
  full?: boolean,
  maxLength: number = 20
) => {
  let members = getOtherMembers(call);
  let callName: string = "";

  for (let member of members) {
    callName += getFullName(member);

    if (members.length > 1) {
      callName += ", ";
    }
  }

  if (full) {
    return callName;
  } else {
    return shorten(callName, maxLength);
  }
}

export const getMessageById = (
  conversation: IConversation,
  messageId?: string
) => {
  if (messageId) {
    return conversation.messages.find((message) => message.id === messageId);
  }
}

/**
 * Convert unicode to native emoji
 *
 * @param unicode - emoji unicode
 */
export const unicodeToEmoji = (unicode: string) => {
  return unicode
    .split("-")
    .map((hex) => parseInt(hex, 16))
    .map((hex) => String.fromCodePoint(hex))
    .join("");
}

export const delay = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms));

export const setLocale = (locale: string) => {
  const t = i18n.global;
  t.locale.value = locale || t.locale.value;
}

export const timeAgo = (date?: Date): string => {
  if (!date) return "unknown"
  moment.locale(useStore().settings!!.preferredLanguage)
  const zonedDate = moment.utc(date).tz(Intl.DateTimeFormat().resolvedOptions().timeZone)
  return zonedDate.fromNow()
}

export const dayFormat = (date: Date, format: string): string => {
  moment.locale(useStore().settings!!.preferredLanguage)
  const zonedDate = moment.utc(date).tz(Intl.DateTimeFormat().resolvedOptions().timeZone)
  return zonedDate.format(format)
}

export const isSameDay = (now: Date, then: Date): boolean => {
  now = new Date(now)
  then = new Date(then)

  return now.getFullYear() === then.getFullYear() &&
      now.getMonth() === then.getMonth() &&
      now.getDate() === then.getDate();
}

export const textMessage = (content: string, sender: IContact): IMessage => {
  return { id: "", content: content, date: new Date(), sender: sender, state: "sending", type: "text"};
}

export const ApiErrorMessageKeys: { [key in keyof typeof ApiErrorCode]: string } = {
  ProfileNotFound: "errors.no.matching.profile.credentials",
  UsernameAlreadyExists: "errors.username.already.exists",
  UsernameNotFound: "errors.invalid.credentials",
  UsernameOrPasswordIncorrect: "errors.invalid.credentials",
  ConnectionAlreadyExists: "errors.connection.already.exists",
  ConversationNotFound: "errors.conversation.not.found",
  ConversationMessageNotFound: "errors.conversation.message.not.found",
  Unknown: "errors.global.error"
}

export const getApiErrorMessageKey = (code: any): string => {
  return ApiErrorMessageKeys[code];
}

