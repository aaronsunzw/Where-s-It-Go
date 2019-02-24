<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

namespace App\Models\Services;
use Illuminate\Database\Eloquent\Model;
use Carbon\Carbon;
use App\Models\Entities\Record;
use Hash;
use App\Models\Services\AuthManager;
use Illuminate\Support\Facades\DB;

/**
 * Description of RecordManager
 *
 * @author aaron
 */
class RecordManager {
    //put your code here
    public function __construct(AuthManager $authManager) {
        $this->AuthManager = $authManager;
    }
    
    public function createRecord($data) {
        try {
            $this->response = $this->AuthManager->auth($data["username"], $data['password']);
            if ($this->response['auth'] == '1') {
                $record = new Record;
                $record->category_id = $data['category_id'];
                $record->record_title = $data['record_title'];
                $record->record_description = $data['record_description'];
                if(isset($data['record_image']))
                {
                  $record->record_image = $data['record_image'];
                }
                
                 if(isset($data['record_audio']))
                {
                  $record->record_audio = $data['record_audio'];
                }
                $record->save();
                $this->response['response']['status'] = '1';
                $this->response['response']['message'] = 'Record successfully created';
            } else {
                $this->response['response']['status'] = '0';
                $this->response['response']['message'] = 'You are not authorized';
            }
        } catch (\Exception $e) {
            $this->response['response']['status'] = '0';
            $this->response['message'] = $e->getMessage();
            return array("json" => $this->response);
        }
        return array("json" => $this->response);
    }
    
    //Retrieve records by category
    public function retrieveRecords($data) {
        //status =0, failed generic error
        //status =1  retrieved successfully
        try {
            $this->response = $this->AuthManager->auth($data["username"], $data["password"]);
            if ($this->response["auth"] == "1") {
                $allRecords = Record::where('category_id', '=', $data['category_id'])->get();
                
                if(count($allRecords) != 0)
                {
                    $recordArray = array();
                    
                    for ($i = 0; $i < count($allRecords); $i++) {
                        $record = $allRecords[$i];
                        $recordArray[$i] = $allRecords[$i];
                    }
                    
                    $this->response['response']['record'] = $recordArray;
                    $this->response["response"]['message'] = "Successfully retrieved ";
                    
                }else
                    $this->response["response"]['message'] = "No Records";
                
                $this->response["response"]["status"] = "1";
            } else {
                $this->response['response']['status'] = '0';
                $this->response['message'] = "Please Login";
            }
        } catch (\Exception $e) {
            $this->response["response"] = array("message" => "Retrieve error", "status" => '0');
            $this->response["response"]["message"] = $e->getMessage();
        }
        
        return array("json" => $this->response);
    }
    
    //delete record
    public function deleteRecord($data){
        try {
            $this->response = $this->AuthManager->auth($data["username"], $data["password"]);
            if ($this->response["auth"] == "1") {
                Record::where('record_id', '=', $data['record_id'])
                        ->where('category_id', '=', $data['category_id'])
                        ->delete();
                
                $this->response["response"]['message'] = "Record Successfully deleted.";
                $this->response["response"]["status"] = "1";
            } else {
                $this->response['response']['status'] = '0';
                $this->response['message'] = "Please Login";
            }
        } catch (\Exception $e) {
            $this->response["response"] = array("message" => "Retrieve error", "status" => '0');
            $this->response["response"]["message"] = $e->getMessage();
        }
        
        return array("json" => $this->response);
    }
    
    //Retrieve all records for user
    public function retrieveAllRecords($data) {
        //status =0, failed generic error
        //status =1  retrieved successfully
        try {
            $this->response = $this->AuthManager->auth($data["username"], $data["password"]);
            if ($this->response["auth"] == "1") {
                $allRecords = DB::table('records')
                        ->join('categories',  'records.category_id', '=', 'categories.category_id')
                        ->where('categories.user_id', '=', $data['user_id'])->get();
                
                if(count($allRecords) != 0)
                {
                    $recordArray = array();
                    
                    for ($i = 0; $i < count($allRecords); $i++) {
                        $record = $allRecords[$i];
                        $recordArray[$i] = $allRecords[$i];
                    }
                    
                    $this->response['response']['record'] = $recordArray;
                    $this->response["response"]['message'] = "Successfully retrieved ";
                    
                }else
                    $this->response["response"]['message'] = "No Records";
                
                $this->response["response"]["status"] = "1";
            } else {
                $this->response['response']['status'] = '0';
                $this->response['message'] = "Please Login";
            }
        } catch (\Exception $e) {
            $this->response["response"] = array("message" => "Retrieve error", "status" => '0');
            $this->response["response"]["message"] = $e->getMessage();
        }
        
        return array("json" => $this->response);
    }
}
