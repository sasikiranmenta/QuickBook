import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';

const routes: Routes = [
    {
        path: 'huid',
        loadChildren: () => import('./huid/huid.module').then( m => m.HuidPageModule)
    },
  {
    path: 'invoice',
    loadChildren: () => import('./invoice/invoice.module').then( m => m.InvoicePageModule)
  },
  {
    path: '',
    redirectTo: 'invoice',
    pathMatch: 'full'
  },
  {
    path: 'viewInvoices',
    loadChildren: () => import('./view-invoice/view-invoice.module').then( m => m.ViewInvoicePageModule)
  }
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { preloadingStrategy: PreloadAllModules, useHash: true })
  ],
  exports: [RouterModule]
})
export class AppRoutingModule { }
