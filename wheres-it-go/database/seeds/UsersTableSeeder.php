<?php

use Illuminate\Database\Seeder;

class UsersTableSeeder extends Seeder
{
    /**
     * Run the database seeds.
     *
     * @return void
     */
    public function run()
    {
        //User1
        DB::table('users')->insert([
            'name' => 'aaron',
            'username' => 'AARON',
            'email' => 'aaronsunzw@gmail.com',
            'password' => bcrypt('Aaron1234'),
        ]);
    }
}
