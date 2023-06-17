import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { HuidPage } from './huid-page.component';

const routes: Routes = [
  {
    path: '',
    component: HuidPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class HuidPageRoutingModule {}
