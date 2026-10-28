import { registerPlugin, Capacitor } from '@capacitor/core';

type NativeActionsPlugin = {
  openDialer(options?: { phone?: string }): Promise<void>;
  openApp(options: { packageName: string }): Promise<void>;
  openWhatsApp(): Promise<void>;
};

const NativeActions = registerPlugin<NativeActionsPlugin>('NativeActions');

export function isNativeAndroid(): boolean {
  return Capacitor.isNativePlatform() && Capacitor.getPlatform() === 'android';
}

export async function openPhoneDialer(phone?: string): Promise<void> {
  if (!isNativeAndroid()) throw new Error('This action requires the installed NB Android app.');
  await NativeActions.openDialer(phone ? { phone } : {});
}

export async function openWhatsApp(): Promise<void> {
  if (!isNativeAndroid()) throw new Error('This action requires the installed NB Android app.');
  await NativeActions.openWhatsApp();
}

export async function openInstalledApp(packageName: string): Promise<void> {
  if (!isNativeAndroid()) throw new Error('This action requires the installed NB Android app.');
  if (!packageName.trim()) throw new Error('An app package name is required.');
  await NativeActions.openApp({ packageName });
}
