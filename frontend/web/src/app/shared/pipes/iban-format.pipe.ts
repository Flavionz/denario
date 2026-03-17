import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'ibanFormat',
  standalone: true,
})
export class IbanFormatPipe implements PipeTransform {
  transform(value: string | null | undefined): string {
    if (!value) return '';
    // Remove existing spaces and format in groups of 4
    const cleaned = value.replace(/\s/g, '').toUpperCase();
    return cleaned.match(/.{1,4}/g)?.join(' ') ?? cleaned;
  }
}
