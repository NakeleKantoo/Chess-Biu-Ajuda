import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'sanPad',
  standalone: true,
})
export class SanPadPipe implements PipeTransform {
  transform(value: string | null | undefined, size: number = 8): string {
    const san = value ?? '';
    return san.padStart(size, '\u00A0');
  }
}
