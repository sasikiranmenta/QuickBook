import { Pipe, PipeTransform } from '@angular/core';

@Pipe({name: 'indianCurrency'})
export class IndianCurrency implements PipeTransform {
  public transform(value: number, symbol: boolean = true): string {

    if (! isNaN(value)) {
      const currencySymbol = symbol?'₹':'';
      // var output = Number(input).toLocaleString('en-IN');   <-- This method is not working fine in all browsers!
      const result = value.toString().split('.');

      let lastThree = result[0].substring(result[0].length - 3);
      const otherNumbers = result[0].substring(0, result[0].length - 3);
      if (otherNumbers !== '') {
        lastThree = ',' + lastThree; }
      let output = otherNumbers.replace(/\B(?=(\d{2})+(?!\d))/g, ',') + lastThree;

      if (result.length > 1) {
        output += '.' + result[1];
      }

      return currencySymbol + output;
    }

  }
}
