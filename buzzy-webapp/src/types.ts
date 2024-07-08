export interface IToken {
  accessToken: string,
  refreshToken: string,
  expiresAt: Date;
}

export interface IUser {
  id: string;
  username: string;
  firstname: string;
  lastname: string;
  email: string;
  avatar?: string;
  lastSeen?: Date;
  contacts?: IContact[];
  settings?: ISettings;
}

export interface IContact {
  id: string;
  firstname: string;
  lastname: string;
  avatar: string;
  username: string;
  email: string;
  lastSeen: Date;
}

export interface IPreviewData {
  title: string;
  image?: string;
  description: string;
  domain: string;
  link: string;
}

export interface IAttachment {
  id: string;
  type: string;
  name: string;
  size: string;
  url: string;
  thumbnail?: string;
  file?: File;
}

export interface IRecording {
  id: string;
  size: string;
  src: string;
  duration: string;
  file?: File;
}

export interface IMessage {
  id: string;
  type?: string;
  content?: string | IRecording;
  date: Date;
  sender: IContact;
  replyTo?: string;
  previewData?: IPreviewData;
  attachments?: IAttachment[];
  state: string;
}

export interface IConversation {
  id: string;
  type: string;
  name?: string;
  avatar?: string;
  admins?: string[];
  contacts: IContact[];
  messages: IMessage[];
  pinnedMessage?: IMessage;
  pinnedMessageHidden?: boolean;
  replyMessage?: IMessage;
  unread?: number;
  draftMessage: string;
}

export interface IContactGroup {
  letter: string;
  contacts: IContact[];
}

export interface INotification {
  flag: string;
  title: string;
  message: string;
}

export interface ISettings {
  lastSeen: boolean;
  readReceipt: boolean;
  joiningGroups: boolean;
  privateMessages: boolean;
  darkMode: boolean;
  borderedTheme: boolean;
  allowNotifications: boolean;
  keepNotifications: boolean;
  preferredLanguage: string;
}

export interface ICall {
  type: string;
  direction: string;
  status: string;
  date: Date;
  length: string;
  members: IContact[];
  adminIds: string[];
}

export interface IEmoji {
  n: string[];
  u: string;
  r?: string;
  v?: string[];
}

export interface MessageBody {
  messageType: string;
  body: string;
}

export interface Messages {
  [key: string]: MessageBody[];
}

export interface SignupForm {
  username: string,
  firstname: string,
  lastname: string,
  password: string,
  confirmPassword: string
}

export interface SigninForm {
  username: string,
  password: string,
  grant_type: string
}

export interface TokensResponse {
  access_token: string;
  refresh_token: string;
  expires_in: number;
}

export interface APIResponse<T> {
  data: T | null;
  errors: ApiError[] | null;
}

export interface ApiError {
  field: string;
  message: string;
  code?: string | null; // Optional property
}

export function emptyResponse<T>(): APIResponse<T> {
  return {
    data: null,
    errors: null,
  };
}
